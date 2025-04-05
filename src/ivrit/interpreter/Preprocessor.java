package ivrit.interpreter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ivrit.interpreter.IvritStreams.JumpingSourceFileReader;
import ivrit.interpreter.UserIO.IvritIO;
import ivrit.interpreter.Variables.ArgumentData;

/**
 * This reads an entire source file and processes whatever it needs for the interpreter to work later.
 */
public class Preprocessor {
    // The file to be preprocessed
    private SourceFile sourceFile; 
    // The io object used for writing to the user.   
    private IvritIO io;
    // Contains a map that connects the titles for jumps,
    // to how many lines need to be skipped in order to get to the correct line of code.
    private Map<String, Integer> jumpMap;
    // Maps between the function name and the line it's code starts at.
    private Map<String, Integer> funcMap;

    /**
     * Constructor.
     * @param file - The file to be preprocessed.
     */
    public Preprocessor(SourceFile sourceFile, IvritIO io){ //Maybe create a PreprocessingFailedException and throw that?
        this.sourceFile = sourceFile;
        this.io = io;
        this.jumpMap = new HashMap<>();
        this.funcMap = new HashMap<>();
    }

    /**
     * Starts to preprocess the file.
     * @throws GeneralPreprocessingException when an exception that can't be traced happened during the prepocessing stage.
     */
    public Map<String, List<ArgumentData>> start() {
        try {
            JumpingSourceFileReader reader = new JumpingSourceFileReader(sourceFile);
            
            io.print("מתחיל עיבוד מקדים של הקובץ.");

            // Maps between the function name and the information about it's arguments.
            Map<String, List<ArgumentData>> functionDeclarations = new HashMap<>();
            int linesCounter = 1;
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                processLine(currentLine, linesCounter, functionDeclarations);
                linesCounter++;
            }

            io.print("העיבוד המקדים הסתיים.");
            return functionDeclarations;
        } catch(IOException exception) {
            //We can't really recover if we can't read from the source file...
            throw new UncheckedIOException("שגיאה: העיבוד המקדים נכשל. בדוק שהקובץ אכן בפורמט הנכון!", exception);
        }
    }

    /**
     * Preprocesses a single line of the source file.
     * @param currentLine - The current line to be processed.
     * @param linesCounter - The number of the curren line.
     * @param functionDeclarations - A map that keeps information about function declarations.
     * @throws IOException when a function declaration is invalid (shorter then 6 words or doesn't contain "מחזירה" or "מקבלת").
     */
    private void processLine(String currentLine, int linesCounter, 
                             Map<String, List<ArgumentData>> functionDeclarations) throws IOException {
        if (currentLine.charAt(0) == Interpreter.JUMP_FLAG_CHAR) { // Handle jumps:
            this.jumpMap.put(currentLine.substring(1), linesCounter);
            return;
        }

        if (currentLine.startsWith(Interpreter.FUNCTION_PREFIX)) { // Handle function definitions:
            List<String> lineWords = new LinkedList<>(Arrays.asList(currentLine.split(" ")));
            if (lineWords.size() < 6 || (!currentLine.contains("מקבלת")) || (!currentLine.contains("מחזירה")))
                throw new IOException("הגדרת הפונקציה בשורה " + linesCounter + " אינה תקינה.");

            String returnType = lineWords.remove(lineWords.size() - 1).trim(); // currently unused.
            lineWords.remove(lineWords.size() - 1);

            this.funcMap.put(lineWords.get(1), linesCounter);
            functionDeclarations.put(lineWords.get(1), processFunctionDeclaration(lineWords));
        }
    }

    private List<ArgumentData> processFunctionDeclaration(List<String> lineTokens) {
        List<ArgumentData> results = new LinkedList<>();
        if (lineTokens.get(3).equals("כלום"))
            return results;

        int curr = 3;
        while (curr < lineTokens.size()) {
            boolean isList = false;
            if (lineTokens.get(curr).equals("רשימה")) {
                isList = true;
                curr++;
            }

            if (curr > lineTokens.size() - 2)
                throw new IllegalArgumentException("שגיאה: הקריאה לפונקציה '" + lineTokens.get(1) + "' אינה חוקית. בדקו שהחתימה חוקית.");

            String type = lineTokens.get(curr++);
            String name = lineTokens.get(curr++);
            if (name.charAt(name.length()-1) == ',')
                name = name.substring(0, name.length()-1);

            results.add(new ArgumentData(name, type, isList));
        }

        return results;
    }

    /**
     * @return a jumper object for the preprocessed file.
     */
    public Jumper generateJumper() {
        return new Jumper(this.jumpMap, this.funcMap);
    }

    /**
     * Prints the entries of the jump map.
     * Mainly used for testing.
     */
    public void printJumpMap() {
        io.print("מפת הקפיצות:");
        for (Map.Entry<String, Integer> entry : this.jumpMap.entrySet()) {
            io.print(entry.getKey() + " : " + entry.getValue());
        }
        io.print("************");
    }


}
