package IvritExceptions.PreprocessingExceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An unchecked exception that may occure during preprocessing.
 */
public class GeneralPreprocessingException extends UncheckedIOException{
    /**
     * Constructor.
     * @param exception - The cause.
     */
    public GeneralPreprocessingException(IOException exception) {
        super("העיבוד המקדים נכשל. בדוק שהקובץ אכן בפורמט הנכון", exception);
    }
    
}
