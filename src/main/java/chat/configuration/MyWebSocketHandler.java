package chat.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import chat.model.BaseEntity;
import chat.model.ChatMessage;
import chat.model.ChannelEntity;
import chat.model.ChatRoomMemberEntity;
import chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {
    private static List<WebSocketSession> list = new ArrayList<>();
    @Autowired
    ChatService chatService;
    private final ObjectMapper objectMapper;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload : {}", payload);
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        chatService.handlerActions(session, chatMessage);
    }
    //* Client가 접속 시 호출되는 메서드 *//*
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        list.add(session);
        log.info(session + " 클라이언트 접속");
        log.info("전체세션 : " +list.toString());
    }
    //* Client가 접속 해제 시 호출되는 메서드 *//*
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(session + " 클라이언트 접속 해제");
        list.remove(session);
        ChatRoomMemberEntity crme = chatService.chatRoomMemberFindBySession(session.getId()).orElseGet(() -> ChatRoomMemberEntity.builder().build()); //현재 방 나간 멤버 정보
        chatService.exitRoom(crme.getUserCd(), crme.getChannelId());
        List<ChatRoomMemberEntity> crmeArr = chatService.chatRoomMemberFindByChannelIdAndConnectYn(crme.getChannelId(), 1);
        
        if(crmeArr.size()==0) {
        	ChannelEntity cre = chatService.chatRoomFindByChannelId(crme.getChannelId()).get();
        	ChannelEntity creUpdate = ChannelEntity.builder().channelId(cre.getChannelId()).channelName(cre.getChannelName()).deleteYn(-1).build();
        	chatService.chatRoomSave(creUpdate);
        }
        log.info("전체세션 : " +list.toString());
    }
}