package IvritExceptions.JumpExceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An uncheked exception that might occure when using a jumper.
 */
public class GeneralJumpException extends UncheckedIOException {
    /**
     * Constructor.
     * @param jumpFlag - The name of the jump flag that the user attempted to jump to.
     * @param cause - What caused this exception.
     */
    public GeneralJumpException(String jumpFlag, IOException exception) {
        super("הקפיצה נתקלה בשגיאה אל נקודת הקפיצה " + jumpFlag, exception);
    }
    
}
