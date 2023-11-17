package IvritExceptions.InterpreterExceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An exception that might occure during the interpretation stage.
 */
public class GeneralInterpreterException extends UncheckedIOException{
    /**
     * Constructor.
     * @param exception - What caused this exception.
     */
    public GeneralInterpreterException(IOException exception) {
        super("המפרש נכשל במהלך הריצה. ודאו שהקובץ אכן בפורמט הנכון", exception);
    }
}
