package IvritExceptions.InterpreterExceptions;

/**
 * An exception that occures if during an interpretation of a line we received an unfamiliar action.
 */
public class UnsupportedActionException extends UnsupportedOperationException {
    /**
     * Constructor.
     * @param attemptedAction - The action the user attempted.
     */
    public UnsupportedActionException(String attemptedAction) {
        super("הפירוש נתקע במילה הלא מוכרת " + attemptedAction);
    }
}
