package IvritExceptions.JumpExceptions;

/**
 * An unchecked exception that occures when telling a Jumper object to jump to a non existing jump flag.
 */
public class JumpFlagNotFoundException extends NullPointerException{
    /**
     * Constructor.
     * @param jumpFlag - The name of the jump flag that the user attempted to jump to.
     */
    public JumpFlagNotFoundException(String jumpFlag) {
        super("לא נמצאה נקודת קפיצה בשם " + jumpFlag);
    }
}
