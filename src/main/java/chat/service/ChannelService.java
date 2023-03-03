package chat.service;

import chat.model.*;
import chat.repository.ChannelMessageRepository;
import chat.repository.ChannelUserRepository;
import chat.repository.ChannelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelService {
    private final ObjectMapper objectMapper;
    private ArrayList<ChannelEntity> ChannelEntityArr;
    private Set<WebSocketSession> sessions = new HashSet<>();
    
    @Autowired ChannelRepository channelRepository;
    @Autowired ChannelUserRepository channelUserRepository;
    @Autowired ChannelMessageRepository channelMessageRepository;

    @PostConstruct
    private void init() {
    	ChannelEntityArr = new ArrayList<ChannelEntity>();
    }
    
    //웹소켓 요청이 왔을때 처리되는 서비스
    public void handlerActions(WebSocketSession session, ChatMessage chatMessage) {
        if (chatMessage.getMessageType().equals(ChatMessage.MessageType.ENTER)) {
            sessions.add(session);
            channelUserSave( 
        		ChannelUserEntity.builder()
        			.channelId(chatMessage.getChannelId())
        			.userId(chatMessage.getUserId())
                    .sessionId(session.getId())
                    .connectYn(1)
                    .build()
            );
            chatMessage.setMessage(chatMessage.getUserId() + "님이 입장했습니다.");
            sendMessage(chatMessage);
        }
    }
    
    //메시지 전송 처리 서비스
    public <T> ResponseEntity sendMessage(ChatMessage chatMessage) {
    	ChannelResponse chatResponse;
    	Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        sessions.parallelStream()
        .forEach(session -> {
        	List<ChannelUserEntity> crme = new ArrayList<ChannelUserEntity>();
        	if(!"".equals(chatMessage.getToUserId())&&chatMessage.getToUserId()!=null){
        		System.out.println("touserId != null");
        		crme = channelUserFindByChannelIdAnduserIdAndConnectYn(chatMessage.getChannelId(), chatMessage.getToUserId(), 1);
        	}else {
        		System.out.println("touserId == null");
        		crme = channelUserFindByChannelIdAndConnectYn(chatMessage.getChannelId(), 1);
        	}
        	if(crme.stream().filter(member -> session.getId().equals(member.getSessionId())).findAny().isPresent()) {
        		try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
        	};
        });
        //채팅 로그를 저장한다.
        ChannelMessageEntity chatEntity = chatSave(
    		chatMessage.toEntity()
		);
        
        resultMap.put("chatMessage",chatMessage);
        
        chatResponse = ChannelResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("요청 성공")
                .result(resultMap).build();
        return ResponseEntity.ok().body(chatResponse);
        
        //requestClient("http://"+chatMessage.getDomain()+"/chat/socketEndpoint.jsp", chatEntity);
    }

    //방을 생성하는 서비스
    public ResponseEntity createChannel(String channelName) {
        ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
            String randomId = UUID.randomUUID().toString();
            ChannelEntity channelEntity = ChannelEntity.builder().channelName(channelName).sessionId(randomId).deleteYn(-1).build();
            ChannelEntityArr.add(channelEntity);  
            channelSave(channelEntity);
            
            resultMap.put("channel",channelEntity);
            
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    //방을 생성하는 서비스
    public ResponseEntity createChannelWithUser(String LOGIN_USER_ID, String TO_USER_ID) {
        ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
            String randomId = UUID.randomUUID().toString();
            ChannelEntity channelEntity = ChannelEntity.builder().sessionId(randomId).deleteYn(-1).build();
            ChannelEntityArr.add(channelEntity);  
            channelEntity = channelSave(channelEntity);
            
            channelUserRepository.save(ChannelUserEntity.builder().channelId(channelEntity.getChannelId()).userId(LOGIN_USER_ID).build());
            channelUserRepository.save(ChannelUserEntity.builder().channelId(channelEntity.getChannelId()).userId(TO_USER_ID).build());
            
            resultMap.put("channel",channelEntity);
            
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    //모든 활성화된 방 리스트를 조회하는 서비스
    public ResponseEntity activeMyChannelList() {
        ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	resultMap.put("channels",channelFindByDeleteYn(-1));
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    //모든 활성화된 방 리스트를 조회하는 서비스
    public ResponseEntity activeChannelList() {
        ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	resultMap.put("channels",channelFindByDeleteYn(-1));
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    public ResponseEntity findByChannelId(Long channelId) {
        ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	Optional<ChannelEntity> optChannelEntity = channelFindByChannelId(channelId);
        	ChannelEntity channelEntity = optChannelEntity.orElseGet(() -> ChannelEntity.builder().build());
    
            resultMap.put("channel", channelEntity);
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    //ChannelId를 통해 해당 방의 채팅 기록을 불러오는 서비스
    public ResponseEntity loadChannel(Long channelId) {
    	ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
    	 	resultMap.put("chatArr", chatFindByChannelId(channelId));
    		chatResponse = ChannelResponse.builder()
    				.statusCode(HttpStatus.OK.value())
    				.status(HttpStatus.OK)
    				.message("요청 성공")
    				.result(resultMap).build();
    		return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }	
    }
    
    //userId와 ChannelId를 통해 해당 방의 유저를 퇴장시키는 서비스
    public ResponseEntity exitChannel(String userId, Long channelId) {
    	ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	if(channelUserEntityFindById(userId, channelId).isPresent()) {
        		resultMap.put("channelUserEntity", channelUserRepository
        				.save(ChannelUserEntity.builder().channelId(channelId).userId(userId).connectYn(-1).build()));
        		chatResponse = ChannelResponse.builder()
        				.statusCode(HttpStatus.OK.value())
        				.status(HttpStatus.OK)
        				.message("요청 성공")
        				.result(resultMap).build();
        		return ResponseEntity.ok().body(chatResponse);
        	}else{
        		chatResponse = ChannelResponse.builder()
        				.statusCode(HttpStatus.OK.value())
        				.status(HttpStatus.OK)
        				.message("해당 유저정보가 없습니다.")
        				.result(resultMap).build();
        		return ResponseEntity.ok().body(chatResponse);
        	}
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }	
    }
    
    //룸코드를 통해 각 방의 활성멤버들을 찾아 반환하는 서비스
    public ResponseEntity findActiveMember(Long channelId) {
    	ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
    	 	resultMap.put("chatArr", channelUserFindByChannelIdAndConnectYn(channelId,1));
    		chatResponse = ChannelResponse.builder()
    				.statusCode(HttpStatus.OK.value())
    				.status(HttpStatus.OK)
    				.message("요청 성공")
    				.result(resultMap).build();
    		return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChannelResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    public void requestClient(String url, ChannelMessageEntity chatEntity) {
    	try {
    		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(url);
    		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
    		
    		WebClient webClient = WebClient.builder()
    				.uriBuilderFactory(factory)
    				.baseUrl(url)
    				.build();
    		
    		String uri = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("MESSAGE_ID", chatEntity.getMESSAGE_ID())
    			.queryParam("ChannelId", chatEntity.getChannelId())
				.queryParam("userId", chatEntity.getUserId())
				.queryParam("touserId", chatEntity.getToUserId()==null||chatEntity.getToUserId().equals("") ? "" : chatEntity.getToUserId())
				.queryParam("message", chatEntity.getMessage())
    		  .toUriString();
    		
    		System.out.println(uri);
    		
    		String response = webClient.get()
    				.uri(uri)
    				.retrieve()
    				.bodyToMono(String.class)
    				.block();
    		System.out.println("응답결과 : "+response);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    //[CRUD]channel
    
    //채팅룸 변경 사항 저장하는 메서드
    public ChannelEntity channelSave(ChannelEntity ChannelEntity) {
    	return channelRepository.save(ChannelEntity);
    }
    //룸코드를 통해 채팅방을 조회하는 메서드 
    public Optional<ChannelEntity> channelFindByChannelId(Long channelId){
    	return channelRepository.findByChannelId(channelId);
    }
    //모든 채팅방을 조회하는 메서드
    public List<ChannelEntity> channelFindAll(){
    	return channelRepository.findAll();
    }
    //모든 활성 채팅방을 조회하는 메서드
    public List<ChannelEntity> channelFindByDeleteYn(int deleteYn){
    	return channelRepository.findByDeleteYn(deleteYn);
    }
    
    //[CRUD]channelMember
    
    //채팅 저장하는 메서드
    public ChannelUserEntity channelUserSave(ChannelUserEntity channelUserEntity) {
    	return channelUserRepository.save(channelUserEntity);
    }
    //userId와 ChannelId를 통해 해당하는 유저를 조회하는 메서드
    public Optional<ChannelUserEntity> channelUserEntityFindById(String userId, Long channelId) {
    	return channelUserRepository.findById(ChannelUserId.builder().userId(userId).channelId(channelId).build());
    }
    //ChannelId를 통해 현재 방에 들어와있는 유저를 조회하는 메서드
    public List<ChannelUserEntity> channelUserFindByChannelIdAndConnectYn(Long channelId, int connectYn) {
    	return channelUserRepository.findByChannelIdAndConnectYn(channelId, connectYn);
    }
    //ChannelId를 통해 현재 방에 들어와있는 유저를 조회하는 메서드
    public List<ChannelUserEntity> channelUserFindByChannelIdAnduserIdAndConnectYn(Long channelId, String userId, int connectYn) {
    	return channelUserRepository.findByChannelIdAndUserIdAndConnectYn(channelId, userId, connectYn);
    }
    //session을 통해 유저를 조회하는 메서드	
    public Optional<ChannelUserEntity> channelUserFindBySession(String session) {
    	return channelUserRepository.findBySessionId(session);
    }
    
    //[CRUD]chat
    
    //채팅 저장하는 메서드
    public ChannelMessageEntity chatSave(ChannelMessageEntity chatEntity) {
    	return channelMessageRepository.save(chatEntity);
    }
    //ChannelId를 통해 해당 방의 채팅 기록을 불러오는 메서드
    public List<ChannelMessageEntity> chatFindByChannelId(Long channelId){
    	return channelMessageRepository.findByChannelId(channelId);
    }
    
}