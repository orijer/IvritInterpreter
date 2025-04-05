package ivrit.interpreter;

import java.io.FileNotFoundException;

/**
 * Defines an interface for objects that transform a given string into a valid Sourcefile object.
 */
public interface SourceCodeLoader {
    //The symbol that signifies a comment in Ivrit.
    public static final char COMMENT_SYMBOL = '#';
    
    /**
     * Transforms a given string into a valid SourceFile object.
     * @throws FileNotFoundException when the was a problem transforming the string into a SourceFile object.
     */
    public SourceFile load(String str) throws IllegalArgumentException;
}
