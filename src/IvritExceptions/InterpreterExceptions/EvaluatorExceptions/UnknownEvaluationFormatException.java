package IvritExceptions.InterpreterExceptions.EvaluatorExceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An exception that occures during the evaluation process if when splitting an expression into data, operator, data, 
 * we didn't find exactly 3 strings separated by spaces.
 */
public class UnknownEvaluationFormatException extends UncheckedIOException {
    public UnknownEvaluationFormatException(int wordsFound, String segment, String originalData) {
        super("שגיאת פרומט בשורה " + originalData + ". בקטע שחושב " + segment + "צופה לקבל 3 ערכים ונמצאו "
                + wordsFound, new IOException());
    }
}
