package chat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@EnableWebSocketMessageBroker //STOMP일시
@EnableWebSocket
@RequiredArgsConstructor
@Configuration
//public class MyWebSocketConfig implements WebSocketMessageBrokerConfigurer{  //STOMP일시
public class MyWebSocketConfig implements WebSocketConfigurer {
    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "ws/chat").setAllowedOrigins("*");
    }
}