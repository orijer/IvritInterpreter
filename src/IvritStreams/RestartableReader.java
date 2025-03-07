package IvritStreams;

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

    public int getCurrentLine() throws IOException;
}
