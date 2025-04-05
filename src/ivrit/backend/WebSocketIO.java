package ivrit.backend;

import ivrit.interpreter.UserIO.IvritIO;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class WebSocketIO implements IvritIO {
    // A spring object that allows us to send messages to a specific user.
    private SimpMessagingTemplate messagingTemplate;
    // A unique identifier of the user that allows us to send messages only to it.
    private String sessionID;
    // The code the user wants to run.
    private String code;
    // A queue for incoming inputs. When it is empty and we try to access it, we are blocked until a new item is inserted.
    private final BlockingQueue<String> inputQueue;

    /**
     * Constructor.
     */
    public WebSocketIO(SimpMessagingTemplate messagingTemplate, String sessionID, String code) {
        this.messagingTemplate = messagingTemplate;
        this.sessionID = sessionID;
        this.inputQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void print(String message) {
        // Send the message to the user's destination. 
        // The destination "/queue/output" is defined in our WebSocket configuration.
        messagingTemplate.convertAndSendToUser(this.sessionID, "/queue/output", message);
    }

    @Override
    public String getUserInput() {
        try {
            return this.inputQueue.take();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return "";
        }
    }

    @Override
    public String getCode() {
        return code;
    }
    
    /**
     *  A method to be called when input is received from the client
     */
    public void supplyUserInput(String input) {
        inputQueue.offer(input);
    }
}
