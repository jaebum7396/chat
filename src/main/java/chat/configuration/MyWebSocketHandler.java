package chat.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import chat.service.ChannelService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
@Getter
public final class MyWebSocketHandler extends TextWebSocketHandler {
    
    
    @Autowired ChannelService chatService;
    private final ObjectMapper objectMapper;
    
    //* Client가 접속 시 호출되는 메서드 *//*
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        chatService.handlerActions(session ,'Y');
    }
    
    //* Client가 접속 해제 시 호출되는 메서드 *//*
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    	chatService.handlerActions(session ,'N');
    }
}