package ivrit.interpreter.IvritStreams;

import java.io.Closeable;
import java.io.IOException;

/**
 * A reader from a file, that can be restarted to the first line of the file.
 */
public interface RestartableReader extends Closeable{
    /**
     * @return the next code line (not jump flags, comment) of the file.
     */
    public String readLine() throws IOException;

    /**
     * Restarts the reader at the first line.
     */
    public void restart() throws IOException;

    /**
     * @return the current line number of the reader, meaning how many code lines (not empty and not comments) were 
     * read from the start of the file.
     */
    public int getCurrentLine();
    
    /**
     * Moves the reader so that the next code line to read will be the lineNumber.
     */
    public void goToLine(int lineNumber) throws IOException;
} //TODO: finish documentation here....
