package ivrit.interpreter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import ivrit.interpreter.IvritExceptions.GeneralFileRuntimeException;

import ivrit.interpreter.UserInput.UserInput;
import ivrit.interpreter.Variables.ArgumentData;
import ivrit.interpreter.IvritStreams.RestartableReader;
import ivrit.interpreter.IvritStreams.RestartableBufferedReader;

/**
 * Handles controlling the flow of the Interpretor.
 */
public class FlowController {
    private UserInput userInput;
    //The GUI of the console that the Interpreter uses:
    private IvritInterpreterGUI gui;

    /**
     * Constructor.
     */
    public FlowController() {
        this.userInput = new UserInput();
        this.gui = new IvritInterpreterGUI(this.userInput);
    }

    /**
     * Contains the logic for starting the interpretation process.
     * @throws GeneralFileRuntimeException when an exception that cannot be traces happened during runtime.
     */
    public void startIvritInterpreter(boolean isFirstRun) {
        //We first try to load the file to interpret:
        File sourceFile;
        try {
            sourceFile = handleFileLoad(isFirstRun);
            if (sourceFile == null) //sourceFile is null IFF the user requested to end the program.
                return;
        } catch (Exception exception) {
            //If we get an exception, print it and try again:
            System.out.println("נתקלנו בשגיאה לא מוכרת בזמן השגת כתובת ההרצה");
            System.out.println("ננסה מחדש:");
            startIvritInterpreter(isFirstRun);
            return;
        }

        //A simple test to see that the loading works correctly:
        try (RestartableReader reader = new RestartableBufferedReader(sourceFile)) {
            //The preprocessing stage:
            Preprocessor preprocessor = new Preprocessor(sourceFile);
            Map<String, List<ArgumentData>> functionDefinitions = preprocessor.start();

            //The interpretation stage:
            Interpreter interpreter = new Interpreter(sourceFile, preprocessor.generateJumper(), functionDefinitions, this.userInput);
            interpreter.initializeGlobalVariables();
            interpreter.start();

        } catch (Exception exception) {
            //exception.printStackTrace(); //mainly turned on during debugging
            System.out.println(exception.getMessage());
            System.out.println("\nננסה להריץ קובץ מחדש: ");
            startIvritInterpreter(isFirstRun);
            return;
        }

        startIvritInterpreter(false);
    }

    /**
     * Handles loading the file from the user input.
     */
    private File handleFileLoad(boolean isFirstRun) {
        if (isFirstRun)
            System.out.println("הכנס את כתובת קובץ ההרצה:");
        else {
            System.out.println("אם ברצונך להריץ קובץ נוסף הכנס אותו עכשיו.");
            System.out.println("אחרת, הכנס סגור.");
        }

        this.userInput.waitForNewUserInput();

        String input = this.userInput.getLastUserInput();
        if (input.equals("סגור")) //Handle closing the program from user request:
            return null;

        input = handleCopyingAddsQuotationMark(input);
        Path path = Paths.get(input);
        if (Files.exists(path)) {
            System.out.println("הקובץ נמצא");
            return path.toFile();
        } else {
            System.out.println("הקובץ לא נמצא!");
            //We try this method again recursively:
            return handleFileLoad(isFirstRun);
        }
    }

    /**
     * When you ask windows to copy the path of the file it adds quotation marks around the real path.
     * This method detects if a string is in quotation marks, and if so it returns the substring without them.
     * Else (= the string wasn't in quotation), we return it unchanged.
     * @param original - The original string.
     * @return - A more correct format the path should have.
     */
    private String handleCopyingAddsQuotationMark(String original) {
        if (original.charAt(0) == '"' && original.charAt(original.length() - 1) == '"') {
            return original.substring(1, original.length() - 1);
        }

        return original;
    }
}
