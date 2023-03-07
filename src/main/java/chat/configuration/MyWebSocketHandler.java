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
import chat.model.MessageEntity;
import chat.model.ChannelEntity;
import chat.model.ChannelUserEntity;
import chat.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {
    private static List<WebSocketSession> list = new ArrayList<>();
    @Autowired
    ChannelService chatService;
    private final ObjectMapper objectMapper;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload : {}", payload);
        MessageEntity messageEntity = objectMapper.readValue(payload, MessageEntity.class);
        chatService.handlerActions(session, messageEntity);
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
        ChannelUserEntity crme = chatService.channelUserFindBySession(session.getId()).orElseGet(() -> ChannelUserEntity.builder().build()); //현재 방 나간 멤버 정보
        chatService.exitChannel(crme.getUserCd(), crme.getChannelCd());
        List<ChannelUserEntity> crmeArr = chatService.channelUserFindByChannelCdAndConnectYn(crme.getChannelCd(), 'Y');
        
        if(crmeArr.size()==0) {
        	ChannelEntity cre = chatService.channelFindByChannelCd(crme.getChannelCd()).get();
        	ChannelEntity creUpdate = ChannelEntity.builder().channelCd(cre.getChannelCd()).channelName(cre.getChannelName()).deleteYn('N').build();
        	chatService.channelSave(creUpdate);
        }
        log.info("전체세션 : " +list.toString());
    }
}