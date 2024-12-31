import java.io.File;
import java.io.IOException;

import Evaluation.EvaluationController;

import IvritExceptions.InterpreterExceptions.GeneralInterpreterException;
import IvritExceptions.InterpreterExceptions.UnsupportedActionException;
import IvritExceptions.InterpreterExceptions.UnsupportedCompoundAssignmentException;

import IvritStreams.RestartableBufferedReader;
import IvritStreams.RestartableReader;
import UserInput.UserInput;
import Variables.StringVariable;
import Variables.VariablesController;

/**
 * The class the reads the preprocessed file and interprets it (executes to the console).
 */
public class Interpreter {
    //If this is the first char of a line, then it is a line that can be jumped to from other lines.
    public static final char JUMP_FLAG_CHAR = '@';

    //The file we want to interpret after preprocessing it.
    private File preprocessedFile;
    //The object that handles all the variables of the program:
    private VariablesController variableController;
    //Controls the jump operation:
    private Jumper jumper;
    //The object that evaluates expressions.
    private EvaluationController evaluator;
    //The object that handles getting input from the user:
    private UserInput userInput;

    /**
     * Constructor.
     * @param preprocessedFile - The file we want to interpret after preprocessing it.
     */
    public Interpreter(File preprocessedFile, Jumper jumper, UserInput userInput) {
        this.preprocessedFile = preprocessedFile;
        this.variableController = new VariablesController();
        this.jumper = jumper;
        this.evaluator = new EvaluationController(this.variableController);
        this.userInput = userInput;
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
     * @throws GeneralInterpreterException when an exception that cannot be traced happened during interpretation.
     */
    public void start() {
        System.out.println("מתחיל לפרש את הקובץ: " + this.preprocessedFile.getName());
        boolean continueProcessing = true;

        try (RestartableReader reader = new RestartableBufferedReader(preprocessedFile)) {
            this.jumper.setActiveReader(reader);

            String currentLine;
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
            throw new GeneralInterpreterException(exception);
        }

        if (continueProcessing)
            System.out.println("פירוש הקובץ הסתיים לאחר שנקרא כל הקובץ (לא עברנו דרך 'צא')");
        else
            System.out.println("פירוש הקובץ הסתיים לאחר שעברנו דרך המילה 'צא'");

        
        System.out.println("המשתנים שנותרו לאחר סיום התכנית: ");
        this.variableController.printVariables();
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

        switch (action) {
            case "הדפס":
                processPrintAction(line.substring(line.indexOf(' ') + 1));
                break;
            case "משתנה":
                processVariableAction(line.substring(line.indexOf(' ') + 1));
                break;
            case "קבוע":
                processConstantAction(line.substring(line.indexOf(' ') + 1));
                break;
            case "מחק":
                processDeleteAction(line.substring(line.indexOf(' ') + 1).trim());
                break;
            case "אם":
                processIfAction(line);
                break;
            case "קפוץ-ל":
                processJumpAction(line.substring(line.indexOf(' ') + 1));
                break;
            case "קלוט-ל":
                processInputAction(line.substring(line.indexOf(' ') + 1));
                break;
            case "צא":
                return false;
            default:
                if (this.variableController.isVariable(action)) {
                    //It is a variable, than we are just assinging to it: 
                    
                    //TODO: we might want to support ++ later, which this misses... maybe split it another way?
                    processAssignmentAction(line.substring(line.indexOf(' ') + 1), action);
                } else {
                    //Unable to recognize what the action was:
                    throw new UnsupportedActionException(action);
                }
                break;
        }

        return true;
    }

    /**
     * Processes the print action.
     */
    private void processPrintAction(String data) {
        data = this.evaluator.evaluate(data);
        System.out.println(data);
    }

    /**
     * Processes the action of creating a new variable.
     */
    private void processVariableAction(String data) {
        String[] infoTokens = splitVariableInfo(data);
        boolean isList = (infoTokens[3].equals("true"));

        if (!isList) { // TODO: support lists here. this will allow us to use create new lists from existing lists, and use existing variables when creating a list.
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

        infoTokens[2] = this.evaluator.evaluate(infoTokens[2]);
        this.variableController.createVariable(infoTokens[0], infoTokens[1], infoTokens[2], isList, true);
    }

    /**
     * @return Splits the information about the variable to 3 tokens and returns them as an array:
     * [0]: the name of the variable.
     * [1]: the type of the variable.
     * [2]: the value of the variable.
     */
    private String[] splitVariableInfo(String variableInfo) {
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
        } else {
            infoTokens[1] = firstWord;
            variableInfo = variableInfo.substring(cutAt + 1).trim();
            cutAt = variableInfo.indexOf('=');
            infoTokens[0] = variableInfo.substring(0, cutAt - 1).trim();
            infoTokens[2] = variableInfo.substring(cutAt + 1).trim();
            infoTokens[3] = "false";
        }

        return infoTokens;
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
     */
    private void processIfAction(String line) {
        String condition = line.substring(line.indexOf("אם") + 2, line.indexOf("אז")).trim();
        condition = this.evaluator.evaluate(condition);

        //Get the flags for the different parts:
        String thenFlag = line.substring(line.indexOf("אז") + 2, line.indexOf(',')).trim();
        String elseFlag = line.substring(line.indexOf("אחרת") + 4, line.lastIndexOf(',')).trim();
        String endFlag = line.substring(line.indexOf("בסוף") + 4).trim();

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
     * Processes getting an input from the user and storing it in a variable.
     * @param variableName - The variable to store the input in.
     */
    private void processInputAction(String variableName) {
        this.userInput.waitForNewUserInput();
        String input = this.userInput.getLastUserInput();
        input = this.evaluator.evaluate(input);

        this.variableController.updateVariable(variableName, input);
    }

    /**
     * Processes the assignment action.
     * @param data - The data to assign.
     * @param variableName - The name of the variable to assign to.
     * @throws UnsupportedCompoundAssignmentException when the compound assignment attempted is not supported in Ivrit.
     */
    private void processAssignmentAction(String data, String variableName) {
        char assignmentType = data.charAt(0);
        data = data.substring(1).trim();
        String newValue;

        switch (assignmentType) {
            case '=':
                //Normal assignment:
                newValue = this.evaluator.evaluate(data);
                break;
            case '+':
                //Additive compound assignment:
                data = this.evaluator.evaluate(data.substring(1).trim());
                if (StringVariable.isStringValue(data) || StringVariable.isStringValue(variableName)) {
                    //Adding strings:
                    newValue = this.evaluator.evaluate(variableName + " + " + data);
                } else {
                    //Adding numbers:
                    newValue = this.evaluator.evaluate('(' + data + " + " + variableName + ')');
                }
                break;
            case '-':
                //Subtractive compound assignment:
                data = this.evaluator.evaluate(data.substring(1).trim());
                newValue = this.evaluator.evaluate('(' + data + " - " + variableName + ')');
                break;
            case '*':
                //Multiplicative compound assignment:
                data = this.evaluator.evaluate(data.substring(1).trim());
                newValue = this.evaluator.evaluate('(' + data + " * " + variableName + ')');
                break;
            case '/':
                //Divisitive compound assignment:
                data = this.evaluator.evaluate(data.substring(1).trim());
                newValue = this.evaluator.evaluate('(' + data + " / " + variableName + ')');
                break;
            default:
                throw new UnsupportedCompoundAssignmentException(assignmentType, data);
        }

        this.variableController.updateVariable(variableName, newValue);
    }
}
