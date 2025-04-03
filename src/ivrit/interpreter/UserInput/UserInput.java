package ivrit.interpreter.UserInput;

import java.util.concurrent.CountDownLatch;

/**
 * The class that handles the input from the user.
 */
public class UserInput {
    //this is true IFF the user is currently allowed to send input:
    private volatile boolean isUserInputAllowed;
    //The last input from the user (if there wasn't any input yet, it is null):
    private String lastUserInput;

    public UserInput() {
        this.isUserInputAllowed = false;
        this.lastUserInput = null;
    }

    /**
     * @return the last input from the user.
     * If there wasn't any input yet- we return null
     */
    public String getLastUserInput() {
        return this.lastUserInput;
    }

    /**
     * Updates this object after receiving an input from the user
     * @param newInput - The new last input of the user.
     * @throws UnsupportedOperationException when a new input was received from the user while this object was not expecting it.
     */
    public void newInputReceived(String newInput) {
        if (this.isUserInputAllowed) {
            this.lastUserInput = newInput;
            this.isUserInputAllowed = false;
        } else {
            throw new UnsupportedOperationException("שגיאה: התקבל קלט משתמש ללא הכנה מוקדמת לכך.");
        }

    }

    /**
     * @return true IFF the user can currently send input.
     */
    public boolean getIsUserInputAllowed() {
        return this.isUserInputAllowed;
    }

    /**
     * Enables to user to send input.
     */
    public void allowUserInput() {
        this.isUserInputAllowed = true;
    }

    /**
     * Does nothing until a new input is received from the user.
     */
    public void waitForNewUserInput() {
        allowUserInput();

        CountDownLatch latch = new CountDownLatch(1);
        UserInputWorker worker = new UserInputWorker(this, latch);
        worker.execute();

        try {
            latch.await();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

    }
}
