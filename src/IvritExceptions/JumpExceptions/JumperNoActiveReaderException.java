package IvritExceptions.JumpExceptions;

/**
 * An uncheked exception that occures whenever a jump is called on a Jumper object that has no active reader.
 */
public class JumperNoActiveReaderException extends NullPointerException{
    /**
     * Constructor.
     */
    public JumperNoActiveReaderException() {
        super("קורא ריק נקרא לקפיצה.");
    }
}
