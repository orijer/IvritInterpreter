package ivrit.backend;

import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import ivrit.interpreter.FlowController;
import ivrit.interpreter.SourceCodeLoader;

@Controller // Marks this class as a Spring MVC controller.
public class InterpreterController {
    private SimpMessagingTemplate messagingTemplate;
    //
    private ConcurrentHashMap<String, WebSocketIO> ioMapping;

    /**
     * Constructor.
     */
    public InterpreterController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.ioMapping = new ConcurrentHashMap<>();
    }
    
    @MessageMapping("/interpret")
    public void executeCode(String code, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Principal user = headerAccessor.getUser();
        if (user == null) {
            System.err.println("User Principal is null: message will not be sent.");
            return;
        }

        String username = user.getName();
        WebSocketIO io = new WebSocketIO(messagingTemplate, username, code);
        this.ioMapping.put(username, io);

        SourceCodeLoader codeLoader = new WebSocketSourceCodeLoader();
        FlowController controller = new FlowController(io, codeLoader, false, false);

        // Actually start running the interpreter:
        controller.startIvritInterpreter();

        // Finished the interpretation:
        this.ioMapping.remove(username);
    }

    @MessageMapping("/input") // This method is mapped to messages sent to "/app/input"
    public String handleInput(String input, SimpMessageHeaderAccessor headerAccessor) {
        Principal user = headerAccessor.getUser();
        if (user == null) {
            return "No user";
        }

        WebSocketIO io = this.ioMapping.get(user.getName());
        if (io != null)
            io.supplyUserInput(input);
        
        return "Input Received";
    }
}
