package ivrit.interpreter.IvritExceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An exception that might occure during the runtime of the program, after the source file was located.
 */
public class GeneralFileRuntimeException extends UncheckedIOException {
    /**
     * Constructor.
     * @param cause - What caused this exception.
     */
    public GeneralFileRuntimeException(IOException cause) {
        super("התרחשה שגיאה כללית בזמן הרצת הקובץ.", cause);
    }
    
}
