import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import IvritExceptions.PreprocessingExceptions.GeneralPreprocessingException;

import IvritStreams.RestartableBufferedReader;

/**
 * This reads an entire source file and processes whatever it needs for the interpreter to work later.
 */
public class Preprocessor {
    //The file to be preprocessed
    private File sourceFile;    
    //Contains a map that connects the titles for jumps,
    //to how many lines need to be skipped in order to get to the correct line of code.
    private Map<String, Integer> jumpMap;

    /**
     * Constructor.
     * @param file - The file to be preprocessed.
     */
    public Preprocessor(File file){ //Maybe create a PreprocessingFailedException and throw that?
        this.sourceFile = file;
        this.jumpMap = new HashMap<>();
    }

    /**
     * Starts to preprocess the file.
     * @throws GeneralPreprocessingException when an exception that can't be traced happened during the prepocessing stage.
     */
    public void start() {
        try (RestartableBufferedReader reader = new RestartableBufferedReader(sourceFile)) {
            System.out.println("מתחיל עיבוד מקדים של הקובץ: " + this.sourceFile.getName());

            int linesCounter = 1;
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.charAt(0) == Interpreter.JUMP_FLAG_CHAR) {
                    this.jumpMap.put(currentLine.substring(1), linesCounter);
                }

                linesCounter++;
            }
            System.out.println("העיבוד המקדים הסתיים.");
        } catch(IOException exception) {
            //We can't really recover if we can't read from the source file...
            throw new GeneralPreprocessingException(exception);
        }
    }

    /**
     * @return a jumper object for the preprocessed file.
     */
    public Jumper generateJumper() {
        return new Jumper(this.jumpMap);
    }

    /**
     * Prints the entries of the jump map.
     * Mainly used for testing.
     */
    public void printJumpMap() {
        System.out.println("מפת הקפיצות:");
        for (Map.Entry<String, Integer> entry : this.jumpMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        System.out.println("************");
    }


}
