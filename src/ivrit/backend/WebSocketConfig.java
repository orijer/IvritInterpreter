package ivrit.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration // Marks this as a configuration class
@EnableWebSocketMessageBroker  // Enables WebSocket message handling, backed by a message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     *  This method configures the message broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // The simple in-memory broker will handle messages whose destinations start with "/queue" (for one-to-one) and "/topic" (for broadcast).
        config.enableSimpleBroker("/queue", "/topic");
        
        // All messages from clients with a destination that starts with "/app" will be routed to message-handling methods.
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * This method registers the endpoint clients will use to connect via WebSocket.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setHandshakeHandler(new CustomHandshakeHandler())
            .setAllowedOriginPatterns("https://ivrit-lang.vercel.app")
            .withSockJS();
    }
}
