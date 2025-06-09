package com.ryunezm.enki.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix for messages routed to message broker (e.g., for sending messages to clients)
        config.enableSimpleBroker("/topic");
        // Prefix for messages from clients to the server (e.g., for sending messages to controllers)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint that clients will connect to
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS(); // Use withSockJS for broader browser support
    }
}
