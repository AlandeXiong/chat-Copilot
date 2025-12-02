package com.example.smartmarketing.config;

import com.example.smartmarketing.ws.MarketingAssistantHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Basic WebSocket configuration that registers a single handler at /ws/assistant.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MarketingAssistantHandler assistantHandler;

    public WebSocketConfig(MarketingAssistantHandler assistantHandler) {
        this.assistantHandler = assistantHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(assistantHandler, "/ws/assistant")
                .setAllowedOrigins("http://localhost:5173");
    }
}


