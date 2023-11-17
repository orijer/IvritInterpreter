package IvritExceptions.InterpreterExceptions.EvaluatorExceptions;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * An exception that occures during the evaluation process a '+' string operator was read, 
 * when we were expecting a value to add.
 */
public class IllegalStringPlusOperatorException extends UncheckedIOException {
    public IllegalStringPlusOperatorException(String originalData) {
        super("נמצאה פעולת + במקום מידע להוספה בשורה" + originalData, new IOException());
    }
}
