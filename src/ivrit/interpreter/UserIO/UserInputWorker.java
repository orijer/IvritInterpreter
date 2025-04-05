package ivrit.interpreter.UserIO;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingWorker;

/**
 * A worker that handles waiting for new user input.
 */
public class UserInputWorker extends SwingWorker<Void, Void>{
    //The object that handles the user input itself:
    private UserInput userInput;
    //The latch that helps synchronizing:
    private CountDownLatch latch;

    /**
     * Constructor.
     * @param userInput - Where to read if user sent an input.
     * @param latch - The latch to update.
     */
    public UserInputWorker(UserInput userInput, CountDownLatch latch) {
        this.userInput = userInput;
        this.latch = latch;
    }

    @Override
    protected Void doInBackground() throws Exception {

        while (this.userInput.getIsUserInputAllowed()) {
            //We are waiting for the user to input to the console.
        }

        return null;
    }

    @Override
    protected void done() {
        latch.countDown();
    }
    
}
