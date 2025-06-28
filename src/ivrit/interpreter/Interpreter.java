package ivrit.interpreter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import ivrit.interpreter.Evaluation.EvaluationController;

import ivrit.interpreter.IvritStreams.JumpingSourceFileReader;
import ivrit.interpreter.UserIO.IvritIO;
import ivrit.interpreter.Variables.ArgumentData;
import ivrit.interpreter.Variables.StringVariable;
import ivrit.interpreter.Variables.VariablesController;

/**
 * The class the reads the preprocessed file and interprets it (executes to the console).
 */
public class Interpreter {
    // If this is the first char of a line, then it is a line that can be jumped to from other lines.
    public static final char JUMP_FLAG_CHAR = '@';
    // Every function definition should start with this keyword
    public static final String FUNCTION_PREFIX = "פונקציה ";

    // The file we want to interpret after preprocessing it.
    private SourceFile preprocessedFile;
    // The object that handles all the variables of the program:
    private VariablesController variableController;
    // Controls the jump operation:
    private Jumper jumper;
    // The object that evaluates expressions.
    private EvaluationController evaluator;
    // The object that handles inputs and outputs:
    private IvritIO io;

    /**
     * Constructor.
     * @param preprocessedFile - The file we want to interpret after preprocessing it.
     */
    public Interpreter(SourceFile preprocessedFile, Jumper jumper, Map<String,List<ArgumentData>> functionDefinitions, IvritIO io) {
        this.preprocessedFile = preprocessedFile;
        this.variableController = new VariablesController(functionDefinitions);
        this.jumper = jumper;
        this.evaluator = new EvaluationController(this.variableController);
        this.io = io;
    }

    /**
     * Initializes all the global variables we want, before starting the interpretation.
     */
    public void initializeGlobalVariables() {
        //Initialize global variables for true and false:
        this.variableController.createVariable("אמת", "טענה", "אמת", false, true);
        this.variableController.createVariable("שקר", "טענה", "שקר", false, true);
    }

    /**
     * Starts interpreting the file.
     * @throws UncheckedIOException when an exception that cannot be traced happened during interpretation.
     */
    public void start() {
        io.print("מתחיל לפרש את הקוד.\n");
        boolean continueProcessing = true;

        try {
            JumpingSourceFileReader reader = new JumpingSourceFileReader(preprocessedFile);
            this.jumper.setActiveReader(reader);

            String currentLine;
            // Read the input line by line, an interpret it each time:
            while ((currentLine = reader.readLine()) != null && continueProcessing) {
                //Handles jump flags:
                if (currentLine.charAt(0) == JUMP_FLAG_CHAR) {
                    //If the jump flag marks the end of a "then" if block part, check where to continue reading at:
                    this.jumper.handleJumpFlagLinks(currentLine.substring(1));
                    continue;
                }

                continueProcessing = processLine(currentLine);
            }
        } catch (IOException exception) {
            //We cant really recover if we cant read from the source file...
            throw new UncheckedIOException("שגיאה: המפרש נכשל במהלך הריצה. ודאו שהקוד אכן בפורמט הנכון.", exception);
        }

        if (continueProcessing) {
            io.print("\nפירוש הקוד הסתיים לאחר שנקרא כל הקוד (לא עברנו דרך 'צא')");
        } else
            io.print("\nפירוש הקוד הסתיים לאחר שעברנו דרך המילה 'צא'");
        
        io.print("המשתנים שנותרו לאחר סיום התכנית: ");
        this.variableController.printVariables(this.io);
    }

    /**
     * Figures out what action needs to be performed, and calls it.
     * @return true IFF the program should continue after the given line is processed.
     * @throws UnsupportedActionException when the action of the line is not supported in Ivrit.
     */
    private boolean processLine(String originalLine) {
        String line = originalLine;
        //The first word of each line tells us what the line does (print, if, create a variable, etc...)
        int endAt = line.indexOf(' ');
        if (endAt == -1) //Handle the case where the line only has one word:
            endAt = line.length();
        String action = line.substring(0, endAt);
        InterpreterCommands command = InterpreterCommands.fromString(action);
        
        if (command == null) { // action is not a command:
            if (this.variableController.isVariable(action))
                processAssignmentAction(line.substring(endAt + 1), action); //It is a variable, than we are just assigning to it: 
            else 
                throw new UnsupportedOperationException("שגיאה: הפירוש נתקע במילה הלא מוכרת '" + action + "' בשורה '" + originalLine + "'");

            return true;
        }

        switch (command) {
            case PRINT:
                processPrintAction(line.substring(endAt + 1));
                break;
            case VARIABLE:
                processVariableAction(line.substring(endAt + 1));
                break;
            case CONSTANT:
                processConstantAction(line.substring(endAt + 1));
                break;
            case DELETE:
                processDeleteAction(line.substring(endAt + 1).trim());
                break;
            case IF:
                processIfAction(line);
                break;
            case JUMP:
                processJumpAction(line.substring(endAt + 1));
                break;
            case CALL:
                processCallFunctionAction(line.substring(endAt + 1));
                break;
            case RETURN:
                if (endAt < line.length())
                    processReturnAction(line.substring(endAt + 1));
                else processReturnAction("");
                break;
            case INPUT:
                processInputAction(line.substring(endAt + 1));
                break;
            case ADD:
                processAddAction(line.substring(5).trim());
                break;
            case REMOVE:
                processRemoveAction(line.substring(4).trim());
                break;
            case EXIT:
                return false;
            default:
                throw new UnsupportedOperationException("שגיאה: הפירוש נתקע במילה הלא מוכרת '" + action + "' בשורה '" + originalLine + "'");
        }

        return true;
    }

    /**
     * Processes the print action.
     */
    private void processPrintAction(String data) {
        data = this.evaluator.evaluate(data);
        io.print(data);
    }

    /**
     * Processes the action of creating a new variable.
     */
    private void processVariableAction(String data) {
        String[] infoTokens = splitVariableInfo(data);
        boolean isList = (infoTokens[3].equals("true"));

        if (!isList) {
            // lists dont evaluate to anything...
            infoTokens[2] = this.evaluator.evaluate(infoTokens[2]);
        }

        this.variableController.createVariable(infoTokens[0], infoTokens[1], infoTokens[2], isList, false);
    }

    /**
     * Processes the action of creating a new constant.
     */
    private void processConstantAction(String data) {
        String[] infoTokens = splitVariableInfo(data);
        boolean isList = (infoTokens[3].equals("true"));

        if (!isList) {
            // lists dont evaluate to anything...
            infoTokens[2] = this.evaluator.evaluate(infoTokens[2]);
        }

        this.variableController.createVariable(infoTokens[0], infoTokens[1], infoTokens[2], isList, true);
    }

    /**
     * @return Splits the information about the variable to 3 tokens and returns them as an array:
     * [0]: the name of the variable.
     * [1]: the type of the variable.
     * [2]: the value of the variable.
     * [3]: true IFF the info belongs to a list variable.
     */
    private String[] splitVariableInfo(String variableInfo) {
        String original = variableInfo;
        try {
            String[] infoTokens = new String[4];
            int cutAt = variableInfo.indexOf(' ');

            String firstWord = variableInfo.substring(0, cutAt).trim(); // either a type string or a list string.

            if (firstWord.equals("רשימה")) {
                variableInfo = variableInfo.substring(cutAt + 1).trim(); // ignore the word: רשימה
                cutAt = variableInfo.indexOf(' '); // search where the type word finishes
                infoTokens[1] = variableInfo.substring(0, cutAt).trim();
                variableInfo = variableInfo.substring(cutAt + 1).trim(); // ignore the type word
                cutAt = variableInfo.indexOf('=');
                infoTokens[0] = variableInfo.substring(0, cutAt - 1).trim();
                infoTokens[2] = variableInfo.substring(cutAt + 1).trim();
                infoTokens[3] = "true";

            } else { // regular variables (non lists)
                infoTokens[1] = firstWord;
                variableInfo = variableInfo.substring(cutAt + 1).trim();
                cutAt = variableInfo.indexOf('=');
                infoTokens[0] = variableInfo.substring(0, cutAt - 1).trim();
                infoTokens[2] = variableInfo.substring(cutAt + 1).trim();
                infoTokens[3] = "false";
            }

            return infoTokens;
        } catch (Exception e) {
            throw new UncheckedIOException("שגיאה: נמצאה שגיאה בפענוח השורה '" + original + "'. ודאו שהשורה מתאימה לפורמט: משתנה טיפוס שם = ערך.", new IOException());
        }
    }

    /**
     * Processes the deletion of a variable.
     * @param variableName - The name of the variable to delete.
     */
    private void processDeleteAction(String variableName) {
        this.variableController.deleteVariable(variableName);
    }

    /**
     * Processes the if action.
     * @param line -The line of the if statement.
     * @throws IllegalArgumentException if any of the if-then-else-finally parts are missing.
     */
    private void processIfAction(String line) {
        int ifIndex = line.indexOf("אם");
        if (ifIndex == -1)
            throw new IllegalArgumentException("שגיאה: חסר 'אם' בשורה '" + line + "'.");

        int thenIndex = line.indexOf("אז");
        if (thenIndex == -1)
            throw new IllegalArgumentException("שגיאה: חסר 'אז' בשורה '" + line + "'.");

        String condition = line.substring(ifIndex + 2, thenIndex).trim();
        condition = this.evaluator.evaluate(condition);

        //Get the flags for the different parts:
        int firstCommaIndex = line.indexOf(',');
        if (firstCommaIndex == -1)
            throw new IllegalArgumentException("שגיאה: חסר פסיק אחרי סוף תגית ה-'אז' בשורה '" + line + "'.");

        int secondCommaIndex = line.lastIndexOf(',');
        if (secondCommaIndex == -1)
            throw new IllegalArgumentException("שגיאה: חסר פסיק אחרי סוף תגית ה-'אחרת' בשורה '" + line + "'.");

        int elseIndex = line.indexOf("אחרת");
        if (elseIndex == -1)
            throw new IllegalArgumentException("שגיאה: חסר 'אחרת' בשורה '" + line + "'.");

        int finallyIndex = line.indexOf("בסוף");
        if (finallyIndex == -1)
            throw new IllegalArgumentException("שגיאה: חסר 'אחרת' בשורה '" + line + "'.");
        
        if (firstCommaIndex == secondCommaIndex)
            throw new IllegalArgumentException("שגיאה: חייבים להיות שני פסיקים בשורה '" + line + "'.");

        String thenFlag = line.substring(thenIndex + 2, firstCommaIndex).trim();
        String elseFlag = line.substring(elseIndex + 4, secondCommaIndex).trim();
        String endFlag = line.substring(finallyIndex + 4).trim();

        //Delete the @ at the start of each flag:
        thenFlag = thenFlag.substring(1);
        elseFlag = elseFlag.substring(1);
        endFlag = endFlag.substring(1); 

        if (condition.equals("אמת")) {
            //Remember where to continue after the "then" part, and execute it:
            this.jumper.addJumpFlagLink(elseFlag, endFlag);

        } else {
            //Jump to the "else" part:
            this.jumper.activeReaderJumpTo(elseFlag);
        }
    }

    /**
     * Processes the jump action.
     * @param jumpFlag - The jump flag we want to jump to.
     */
    private void processJumpAction(String jumpFlag) {
        this.jumper.activeReaderJumpTo(jumpFlag);
    }

    /**
     * Processes the function calls action.
     * @param callLine - The line that contains which function was called and with what arguments.
     */
    private void processCallFunctionAction(String callLine) {
        String original = callLine;
        int functionNameEndIndex = callLine.indexOf(' ');
        if (functionNameEndIndex == -1) { // A call without arguments
            this.jumper.activeReaderStartFunction(callLine);
            this.variableController.createScope();
        } else { // A call with arguments. We need to extract them first.
            String functionName = callLine.substring(0, functionNameEndIndex).trim();
            callLine = callLine.substring(functionNameEndIndex + 1).trim();
            if (!callLine.startsWith("עם"))
                throw new IllegalArgumentException("שגיאה: נמצאה קריאה לפונקציה עם ארגומנטים אך ללא המילה עם בשורה '" + original + "'.");
            callLine = callLine.substring(3).trim();
            String[] args = callLine.split(","); //TODO: but this splits strings that include , incorrectly...
            for (int i = 0; i < args.length; i++)
                args[i] = this.evaluator.evaluate(args[i].trim());

            this.jumper.activeReaderStartFunction(functionName);
            this.variableController.createScope(functionName, args);
        }
    }

    /**
     * Processes the return to caller action.
     */
    private void processReturnAction(String returnValue) {
        this.jumper.activeReaderReturnToCaller();
        this.variableController.popScope();
    }

    /**
     * Processes getting an input from the user and storing it in a variable.
     * @param variableName - The variable to store the input in.
     */
    private void processInputAction(String variableName) {
        String input = this.io.getUserInput();
        input = this.evaluator.evaluate(input);

        this.variableController.updateVariable(variableName, input);
    }

    /**
     * Processes adding a value to a list.
     * @param line - The line the describes what value to add, where to add it, and to which list to add it.
     */ //TODO: make sure this works with lists of strings that contain spaces
    private void processAddAction(String line) {
        String target = line.substring(line.lastIndexOf(' ')+1).trim(); 
        line = line.substring(0, line.lastIndexOf(' ')).trim();
        
        if (line.endsWith("לתחילת") || line.endsWith("בתחילת")) {
            line = line.substring(0, line.lastIndexOf(' ')).trim();
            this.variableController.addToListVariable(target, "1", line);

        } else if (line.endsWith("לסוף") || line.endsWith("בסוף")) {
            line = line.substring(0, line.lastIndexOf(' ')).trim();
            this.variableController.addToListVariable(target, "end", line);

        } else if (line.endsWith("של")) { // adding to the middle of a list
            line = line.substring(0, line.lastIndexOf(' ')).trim();
            String[] values = line.split("במקום");
            this.variableController.addToListVariable(target, values[1].trim(), values[0].trim());

        } else {
            throw new UnsupportedOperationException("שגיאה: הפירוש נתקע בקטע הלא החוקי '" + line + "' בזמן הוספה לרשימה.");
        }
    }

    private void processRemoveAction(String line) {
        String target = line.substring(line.lastIndexOf(' ') + 1).trim(); 
        line = line.substring(0, line.lastIndexOf(' ')).trim();

        if (line.endsWith("מתחילת")) {
            this.variableController.removeFromListVariable(target, "1");

        } else if (line.endsWith("מסוף")) {
            this.variableController.removeFromListVariable(target, "end");

        } else if (line.endsWith("של")) { // removing from the middle of a list
            line = line.substring(0, line.lastIndexOf(' ')).trim();
            this.variableController.removeFromListVariable(target, line.substring(line.lastIndexOf(' ')).trim());

        } else {
            throw new UnsupportedOperationException(
                    "שגיאה: הפירוש נתקע בקטע הלא החוקי '" + line + "' בזמן הסרה לרשימה.");
        }
    }

    /**
     * Processes the assignment action.
     * @param data - The data to assign.
     * @param variableName - The name of the variable to assign to.
     * @throws UnsupportedOperationException when the compound assignment attempted is not supported in Ivrit.
     */
    private void processAssignmentAction(String data, String variableName) {
        if (this.variableController.isList(variableName) && data.startsWith("במקום")) {
            // We are trying to assign inside a list:
            data = data.substring(6); // ignore במקום
            int charAt = data.indexOf('=');
            if (charAt == -1)
                throw new UnsupportedOperationException("שגיאה: נמצאה השמה של המשתנה '" + variableName + "' שבה לא היה הסימן שווה (=).");

            int index = Integer.parseInt(data.substring(0, charAt).trim());
            String newValue = this.evaluator.evaluate(data.substring(charAt+1).trim());
            this.variableController.updateListVariable(variableName, index, newValue);
            return;
        }

        // Assignment of a regular variable:
        char assignmentType = data.charAt(0);
        data = data.substring(1).trim();
        String newValue;

        switch (assignmentType) {
            case '=':
                // Normal assignment:
                newValue = this.evaluator.evaluate(data);
                break;
            case '+':
                // Additive compound assignment:
                data = this.evaluator.evaluate(data.substring(1).trim());
                if (StringVariable.isStringValue(data) || StringVariable.isStringValue(variableName)) {
                    // Adding strings:
                    newValue = this.evaluator.evaluate(variableName + " + " + data);
                } else {
                    // Adding numbers:
                    newValue = this.evaluator.evaluate('(' + data + " + " + variableName + ')');
                }
                break;
            case '-':
                // Subtractive compound assignment:
                data = this.evaluator.evaluate(data.substring(1).trim());
                newValue = this.evaluator.evaluate('(' + data + " - " + variableName + ')');
                break;
            case '*':
                // Multiplicative compound assignment:
                data = this.evaluator.evaluate(data.substring(1).trim());
                newValue = this.evaluator.evaluate('(' + data + " * " + variableName + ')');
                break;
            case '/':
                // Divisitive compound assignment:
                data = this.evaluator.evaluate(data.substring(1).trim());
                newValue = this.evaluator.evaluate('(' + data + " / " + variableName + ')');
                break;
            case '%':
                // Modulo compound assignment:
                data = this.evaluator.evaluate(data.substring(1).trim());
                newValue = this.evaluator.evaluate('(' + data + " / " + variableName + ')');
            default:
                throw new UnsupportedOperationException("שגיאה: התו " + assignmentType + " אינו חוקי לפני התו '=' בפעולת השמה בקטע " + data);
        }

        this.variableController.updateVariable(variableName, newValue);
    }
}
