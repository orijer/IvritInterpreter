package ivrit.interpreter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
    public Map<String, List<ArgumentData>> startPreprocessing() {
        try {
            // Turn If-Statements and Loops to simple jumps:
            preprocessIfsAndLoops();

            JumpingSourceFileReader reader = new JumpingSourceFileReader(sourceFile);
            
            io.print("מתחיל עיבוד מקדים של הקוד.");

            // Maps between the function name and the information about it's arguments.
            Map<String, List<ArgumentData>> functionDeclarations = new HashMap<>();
            int linesCounter = 1;
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                preprocessLine(currentLine, linesCounter, functionDeclarations);
                linesCounter++;
            }

            io.print("העיבוד המקדים הסתיים.");
            return functionDeclarations;
        } catch(IOException exception) {
            //We can't really recover if we can't read from the source file...
            throw new UncheckedIOException("שגיאה: העיבוד המקדים נכשל. בדוק שהקוד אכן בפורמט הנכון!", exception);
        }
    }

    /**
    * Turns all If-Statements and Loops in the object's SourceFile to jumps.
    */
    private void preprocessIfsAndLoops() {
        int currentIfLabel = 0;
        int currentLoopLabel = 0;
        int newIfLabel = 0;
        int newLoopLabel = 0;
        Stack<Boolean> isIfFinishStack = new Stack<>();
        List<String> convertedLines = new LinkedList<>();

        for (int lineIndex=0; lineIndex<this.sourceFile.getSourceFileSize(); lineIndex++) {
            String currLine = this.sourceFile.getLine(lineIndex);
            if (currLine.startsWith("אם ")) {
                if (currLine.length() == 3)
                    throw new UncheckedIOException(new IOException("שגיאה: העיבוד המקדים נכשל כי נמצא שימוש ב 'אם' ללא תנאי עוקב בשורה " + (lineIndex + 1)));
                String condition = currLine.substring(3);
                currentIfLabel++;
                newIfLabel++;

                convertedLines.add("אם " + condition + " אז @אם_" + newIfLabel + ", אחרת @אחרת_" + newIfLabel + ", בסוף @סוף_" + newIfLabel);
                convertedLines.add("@אם_" + newIfLabel);
                isIfFinishStack.push(true);
            } else if (currLine.startsWith("אחרת")) {
                convertedLines.add("@אחרת_" + currentIfLabel);
            } else if (currLine.startsWith("כל עוד ")) {
                if (currLine.length() == 7)
                    throw new UncheckedIOException(new IOException("שגיאה: העיבוד המקדים נכשל כי נמצא שימוש ב 'כל עוד' ללא תנאי עוקב בשורה " + (lineIndex + 1)));

                String condition = currLine.substring(7);
                currentLoopLabel++;
                newLoopLabel++;  
                
                convertedLines.add("@לולאה_" + newLoopLabel);
                convertedLines.add("אם " + condition + " אז @גוף_לולאה_" + newLoopLabel + ", אחרת @סוף_לולאה_" + newLoopLabel + ", בסוף @סוף_לולאה_" + newLoopLabel);
                convertedLines.add("@גוף_לולאה_" + newLoopLabel);
                isIfFinishStack.push(false);
            } else if (currLine.startsWith("סוף")) {
                if (isIfFinishStack.isEmpty())
                    throw new UncheckedIOException(new IOException("שגיאה: נמצא 'סוף' מיותר בשורה " + (lineIndex + 1)));

                if (isIfFinishStack.pop()) { // This is the finish of an If-Statement
                    convertedLines.add("@סוף_" + currentIfLabel);
                    currentIfLabel--;
                } else { // This is the finish of a Loop
                    convertedLines.add("קפוץ-ל לולאה_" + currentLoopLabel);
                    convertedLines.add("@סוף_לולאה_" + currentLoopLabel);
                    currentLoopLabel--;
                }
            } else convertedLines.add(currLine);
        }

        if (currentIfLabel > 0)
            throw new UncheckedIOException(new IOException("שגיאה: קיים 'אם' שאינו נסגר בעזרת 'סוף'!"));

        if (currentLoopLabel > 0)
            throw new UncheckedIOException(new IOException("שגיאה: קיימת לולאת 'כל עוד' שאינה נסגרת בעזרת 'סוף'!"));

        this.sourceFile.setLines(convertedLines);
    }

    /**
     * Preprocesses a single line of the source file.
     * @param currentLine - The current line to be processed.
     * @param linesCounter - The number of the curren line.
     * @param functionDeclarations - A map that keeps information about function declarations.
     * @throws IOException when a function declaration is invalid (shorter then 6 words or doesn't contain "מחזירה" or "מקבלת").
     */
    private void preprocessLine(String currentLine, int linesCounter, 
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
            functionDeclarations.put(lineWords.get(1), preprocessFunctionDeclaration(lineWords));
        }
    }

    private List<ArgumentData> preprocessFunctionDeclaration(List<String> lineTokens) {
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
