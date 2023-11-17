package IvritExceptions.InterpreterExceptions.EvaluatorExceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An exception that occures during the evaluation process of a string,
 * if an operator that expected another value after it was read, but the line ended instead.
 */
public class AnotherValueExpectedException extends UncheckedIOException {
    public AnotherValueExpectedException(String originalData) {
        super("ציפינו לקלט נוסף לפני סוף השורה " + originalData, new IOException());
    }
}
