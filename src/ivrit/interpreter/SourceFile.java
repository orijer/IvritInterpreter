package ivrit.interpreter;

import java.util.List;

/**
 * A class that holds the lines of code of an Ivrit code file.
 */
public class SourceFile {
    /** 
     * A list that contains all the lines of code from the source file, in their original order.
     * This means we dont include empty lines or lines that start with # (meaning comment lines).
     */
    List<String> codeLines;

    /**
     * Constructor.
     * @param codeLines - A list of strings that contains all the lines of codes in their respective order.
     */
    public SourceFile(List<String> codeLines) {
        this.codeLines = codeLines;
    }

    /**
     * Sets the lines of this source file to the given value.
     */
    public void setLines(List<String> codeLines) {
        this.codeLines = codeLines;
    }

    /**
     * @param lineNumber - The number of the line to retrieve.
     * @return a line of code at a specific index (number of line).
     * @throws IndexOutOfBoundsException when the given lineNumber is either negative or larger then the number of code lines.
     */
    public String getLine(int lineNumber) throws IndexOutOfBoundsException {
        if (lineNumber < 0)
            throw new IndexOutOfBoundsException("שגיאה: מספר שורה אינו יכול להיות שלילי.");
        
        if (lineNumber >= this.codeLines.size()) 
            throw new IndexOutOfBoundsException("מספר השורה חרג ממספר השורות בקובץ המקור.");

        return this.codeLines.get(lineNumber);
    }

    /**
     * @return the number of code lines in this source file.
     */
    public int getSourceFileSize() {
        return this.codeLines.size();
    }
}
