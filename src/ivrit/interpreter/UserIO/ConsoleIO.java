package ivrit.interpreter.UserIO;

/**
 * The class that handles the IO in the Ivrit console GUI program.
 */
public class ConsoleIO implements IvritIO {
    private UserInput userInput;

    public ConsoleIO(UserInput userInput) {
        this.userInput = userInput;
    }

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public String getUserInput() {
        this.userInput.waitForNewUserInput();
        return this.userInput.getLastUserInput();
    }

    @Override
    public String getCode() {
        // In a ConsoleIO, in order to get the code we receive as input a pth from the user which points to the source code.
        return getUserInput();
    }
}
