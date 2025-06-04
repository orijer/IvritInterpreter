package ivrit.interpreter;

import java.util.List;
import java.util.Map;

import ivrit.interpreter.IvritExceptions.GeneralFileRuntimeException;
import ivrit.interpreter.Variables.ArgumentData;
import ivrit.interpreter.UserIO.IvritIO;

/**
 * Handles controlling the flow of the Interpretor.
 */
public class FlowController {
    private IvritIO io;
    SourceCodeLoader codeLoader;
    boolean keepRunning;
    boolean printMessages;

    /**
     * Constructor.
     */
    public FlowController(IvritIO io, SourceCodeLoader codeLoader) {
        this.io = io;
        this.codeLoader = codeLoader;
        this.keepRunning = true;
        printMessages = true;
    }

    /**
     * Constructor.
     */
    public FlowController(IvritIO io, SourceCodeLoader codeLoader, boolean keepRunning, boolean printMessages) {
        this.io = io;
        this.codeLoader = codeLoader;
        this.keepRunning = keepRunning;
        this.printMessages = printMessages;
    }

    /**
     * Contains the logic for starting the interpretation process.
     * @throws GeneralFileRuntimeException when an exception that cannot be traces happened during runtime.
     */
    public void startIvritInterpreter() {
        startIvritInterpreter(true);
    }

    /**
     * Contains the logic for starting the interpretation process.
     * @param isFirstRun - Used for displaying a more different message after the first ivri program finished running.
     * @throws GeneralFileRuntimeException when an exception that cannot be traces happened during runtime.
     */
    public void startIvritInterpreter(boolean isFirstRun) {
        // We first try to load the file to interpret:
        SourceFile sourceFile;
        try {
            sourceFile = handleFileLoad(isFirstRun);
            if (sourceFile == null) //sourceFile is null IFF the user requested to end the program.
                return;

        } catch (Exception exception) {
            //If we get an unhandled exception, print and try again:
            exception.printStackTrace();
            if (!keepRunning) 
                return;

            if (printMessages) {
                io.print("נתקלנו בשגיאה לא מוכרת בזמן השגת כתובת ההרצה");
                io.print("ננסה מחדש:");
            }

            startIvritInterpreter(isFirstRun);
            return;
        }

        try {
            //The preprocessing stage:
            Preprocessor preprocessor = new Preprocessor(sourceFile, this.io);
            Map<String, List<ArgumentData>> functionDefinitions = preprocessor.start();

            //The interpretation stage:
            Interpreter interpreter = new Interpreter(sourceFile, preprocessor.generateJumper(), functionDefinitions, this.io);
            interpreter.initializeGlobalVariables();
            interpreter.start();

            if (keepRunning)
                startIvritInterpreter(false);

        } catch (Exception exception) {
            //exception.printStackTrace(); //mainly turned on during debugging
            io.print(exception.getMessage());
            if (!keepRunning)
                return;

            if (printMessages)
                io.print("\nננסה להריץ קובץ מחדש: ");
            
            startIvritInterpreter(isFirstRun);
            return;
        }
    }

    /**
     * Handles loading the file from the user input.
     */
    private SourceFile handleFileLoad(boolean isFirstRun) {
        if (printMessages) {
            if (isFirstRun)
                io.print("הכנס את כתובת קובץ ההרצה:");
            else {
                io.print("אם ברצונך להריץ קובץ נוסף הכנס אותו עכשיו.");
                io.print("אחרת, הכנס סגור.");
            }
        }

        String input = this.io.getCode();
        if (input.equals("סגור")) // Handle closing the program from user request:
            return null;

        try {
            return this.codeLoader.load(input);
        } catch (IllegalArgumentException exception) {
            if (printMessages)
                io.print("הקובץ לא נמצא!");
                
            if (keepRunning)
                return handleFileLoad(isFirstRun); // try again
            else return null;
        }
    }
}
