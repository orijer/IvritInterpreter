package IvritStreams;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A buffered reader that reads from a file, and can be restarted to the first line of the file.
 */
public class RestartableBufferedReader implements RestartableReader {
    // We delegate the reading itself to a buffered reader we save as a field:
    private BufferedReader delegatedReader;
    // The file weare reading from:
    private File sourceFile;
    // true IFF this reader is open (= usable):
    private boolean isOpen;
    // The number of the next line to read from the source.
    private int currentLine;

    /**
     * Constructor.
     * @param readFrom - The file this reads from.
     */
    public RestartableBufferedReader(File sourceFile) throws IOException {
        this.delegatedReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(sourceFile), "UTF-8"));
        this.sourceFile = sourceFile;
        this.isOpen = true;
        this.currentLine = 0;
    }

    @Override
    public String readLine() throws IOException {
        if (this.isOpen) {
            String line;
            do {
                line = this.delegatedReader.readLine();
                this.currentLine++;
            } while (line != null && (line.isBlank() || line.charAt(0) == '#'));

            return line;
        }

        throw new IOException("הקורא הזה כבר סגור");
    }

    @Override
    public void restart() throws IOException {
        if (this.isOpen) {
            this.delegatedReader.close();
        }

        this.currentLine = 0;
        this.delegatedReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(sourceFile), "UTF-8"));
    }

    @Override
    public void close() throws IOException {
        if (this.isOpen) {
            this.delegatedReader.close();
            return;
        }

        throw new IOException("הקורא הזה כבר סגור");
    }

    @Override
    public int getCurrentLine() {
        return this.currentLine;
    }

}
