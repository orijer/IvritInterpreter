package ivrit.interpreter.IvritStreams;

import java.io.IOException;

import ivrit.interpreter.SourceFile;

/**
 * A buffered reader that reads from a file, and can be restarted to the first line of the file.
 */
public class JumpingSourceFileReader {
    // An object that contains all of the code lines:
    private SourceFile sourceFile;
    // The number of the next line to read from the source. We only count lines that were neither empty nor comments.
    private int currentLine;

    /**
     * Constructor.
     * @param readFrom - The file this reads from.
     */
    public JumpingSourceFileReader(SourceFile sourceFile) throws IOException {
        this.sourceFile = sourceFile;
        this.currentLine = 0;
    }

    /**
     * @return the next line from the source file. If there are no lines left, just return null.
     */
    public String readLine() {
        if (currentLine == this.sourceFile.getSourceFileSize())
            return null;

        return this.sourceFile.getLine(currentLine++);
    }

    /**
     * Restarts the reader to point at the first line of the source file.
     * Performing readLine() after restarting should return the very first line of the source file.
     */
    public void restart() {
        this.currentLine = 0;
    }

    /**
     * @return the number of the current line of the source file. This is the number of the next returned line.
     */
    public int getCurrentLine() {
        return this.currentLine;
    }

    /**
     * Points this reader to a specific line from the source file, so performing readLine() would now return that line.
     * @param lineNumber - The number of the line to point to.
     * @throws IndexOutOfBoundsException when lineNumber is negative or when it is larger then the number of code lines in the source file.
     */
    public void goToLine(int lineNumber) throws IndexOutOfBoundsException {
        if (lineNumber < 0)
            throw new IndexOutOfBoundsException("שגיאה: אי אפשר לקפוץ לשורה שלילית בקובץ המקור.");

        if (lineNumber > this.sourceFile.getSourceFileSize())    
            throw new IndexOutOfBoundsException("שגיאה: אי אפשר לקפוץ לשורה שלילית בקובץ המקור.");

        this.currentLine = lineNumber;
    }
}
