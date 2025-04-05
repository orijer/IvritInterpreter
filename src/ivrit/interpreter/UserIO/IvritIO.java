package ivrit.interpreter.UserIO;

/**
 * An interface that defines how the IO should work in Ivrit, both for the GUI interpreter and for the backend.
 */
public interface IvritIO {
    /**
     * Sends a message to te user.
     * @param message- The message to be sent.
     */
    public void print(String message);

    /**
     * @return an input string from the user.
     */
    public String getUserInput();

    /**
     * @return a string that holds the source code (whether it be the actual source code or a path to it).
     */
    public String getCode();
}
