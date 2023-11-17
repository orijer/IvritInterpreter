package IvritExceptions.InterpreterExceptions.EvaluatorExceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An exception that occures during the evaluation process if an uneven structure of brackets was found. 
 */
public class UnevenBracketsException extends UncheckedIOException{
    public UnevenBracketsException(String str) {
        super("נמצאה שגיאה באיזון הסוגריים בקטע: " + str, new IOException());
    }
    
}
