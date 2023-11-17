package IvritExceptions.InterpreterExceptions.EvaluatorExceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An exception that occures during the evaluation process of a string, 
 * if there is no '+' before a literal string or a variable (that is not the first in the line)
 */
public class UnexpectedStringException extends UncheckedIOException {
    public UnexpectedStringException(String originalData) {
        super("מחרוזת נכתבה ללא + מתאים לפניה בשורה " + originalData, new IOException());
    }
}
