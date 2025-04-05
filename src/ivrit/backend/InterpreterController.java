package ivrit.backend;

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
    
    @MessageMapping("/interpret") // This method is mapped to messages sent to "/app/interpret"
    @SendToUser("/queue/output") // The output will be sent to the specific user’s destination "/queue/output"
    public void executeCode(String code, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String sessionID = headerAccessor.getSessionId();
        
        WebSocketIO io = new WebSocketIO(messagingTemplate, sessionID, code);
        this.ioMapping.put(sessionID, io);

        SourceCodeLoader codeLoader = new WebSocketSourceCodeLoader();
        FlowController controller = new FlowController(io, codeLoader);

        // Actually start running the interpreter:
        controller.startIvritInterpreter();

        // Finished the interpretation:
        this.ioMapping.remove(sessionID);
    }

    @MessageMapping("/input") // This method is mapped to messages sent to "/app/input"
    @SendToUser("/queue/input")
    public String handleInput(String input, SimpMessageHeaderAccessor headerAccessor) {
        String sessionID = headerAccessor.getSessionId();
        WebSocketIO io = this.ioMapping.get(sessionID);
        if (io != null)
            io.supplyUserInput(input);
        
        return "Input Received";
    }
}
