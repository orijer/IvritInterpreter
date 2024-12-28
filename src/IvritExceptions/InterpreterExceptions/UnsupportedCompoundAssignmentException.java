package IvritExceptions.InterpreterExceptions;

/**
 * An exception that occures if during a compound assignment (assingent like +=), an unsupported modifier was received before '='.
 */
public class UnsupportedCompoundAssignmentException extends UnsupportedOperationException{
    /**
     * Constructor.
     * @param assignmentType - The type of assignment the user attempted.
     * @param data - the rest of the data in that assignment action.
     */
    public UnsupportedCompoundAssignmentException(char assignmentType, String data) {
        super("התו " + assignmentType + " אינו חוקי לפני התו '=' בפעולת השמה בקטע " + data);
    }
}
