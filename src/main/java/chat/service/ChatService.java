package chat.service;

import chat.model.*;
import chat.repository.ChatRepository;
import chat.repository.ChatRoomMemberRepository;
import chat.repository.ChatRoomRepository;
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
public class ChatService {
    private final ObjectMapper objectMapper;
    private ArrayList<ChannelEntity> ChannelEntityArr;
    private Set<WebSocketSession> sessions = new HashSet<>();
    
    @Autowired ChatRepository chatRepository;
    @Autowired ChatRoomRepository chatRoomRepository;
    @Autowired ChatRoomMemberRepository chatRoomMemberRepository;

    @PostConstruct
    private void init() {
    	ChannelEntityArr = new ArrayList<ChannelEntity>();
    }
    
    //웹소켓 요청이 왔을때 처리되는 서비스
    public void handlerActions(WebSocketSession session, ChatMessage chatMessage) {
        if (chatMessage.getMessageType().equals(ChatMessage.MessageType.ENTER)) {
            sessions.add(session);
            chatRoomMemberSave( 
        		ChatRoomMemberEntity.builder()
        			.channelId(chatMessage.getChannelId())
        			.userCd(chatMessage.getUserCd())
                    .session(session.getId())
                    .connectYn(1)
                    .build()
            );
            chatMessage.setMessage(chatMessage.getUserCd() + "님이 입장했습니다.");
            sendMessage(chatMessage);
        }
    }
    
    //메시지 전송 처리 서비스
    public <T> ResponseEntity sendMessage(ChatMessage chatMessage) {
    	ChatResponse chatResponse;
    	Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        sessions.parallelStream()
        .forEach(session -> {
        	List<ChatRoomMemberEntity> crme = new ArrayList<ChatRoomMemberEntity>();
        	if(!"".equals(chatMessage.getToUserCd())&&chatMessage.getToUserCd()!=null){
        		System.out.println("toUserCd != null");
        		crme = chatRoomMemberFindByChannelIdAndUserCdAndConnectYn(chatMessage.getChannelId(), chatMessage.getToUserCd(), 1);
        	}else {
        		System.out.println("toUserCd == null");
        		crme = chatRoomMemberFindByChannelIdAndConnectYn(chatMessage.getChannelId(), 1);
        	}
        	if(crme.stream().filter(member -> session.getId().equals(member.getSession())).findAny().isPresent()) {
        		try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
        	};
        });
        //채팅 로그를 저장한다.
        ChatEntity chatEntity = chatSave(
    		chatMessage.toEntity()
		);
        
        resultMap.put("chatMessage",chatMessage);
        
        chatResponse = ChatResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("요청 성공")
                .result(resultMap).build();
        return ResponseEntity.ok().body(chatResponse);
        
        //requestClient("http://"+chatMessage.getDomain()+"/chat/socketEndpoint.jsp", chatEntity);
    }

    //방을 생성하는 서비스
    public ResponseEntity createRoom(String channelName) {
        ChatResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
            String randomId = UUID.randomUUID().toString();
            ChannelEntity channelEntity = ChannelEntity.builder().channelName(channelName).sessionId(randomId).deleteYn(-1).build();
            ChannelEntityArr.add(channelEntity);  
            chatRoomSave(channelEntity);
            
            resultMap.put("chatRoom",channelEntity);
            
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    //방을 생성하는 서비스
    public ResponseEntity createRoomWithUser(String LOGIN_USER_ID, String TO_USER_ID) {
        ChatResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
            String randomId = UUID.randomUUID().toString();
            ChannelEntity channelEntity = ChannelEntity.builder().sessionId(randomId).deleteYn(-1).build();
            ChannelEntityArr.add(channelEntity);  
            chatRoomSave(channelEntity);
            
            resultMap.put("chatRoom",channelEntity);
            
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    //모든 활성화된 방 리스트를 조회하는 서비스
    public ResponseEntity activeMyRoomList() {
        ChatResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	resultMap.put("chatRooms",chatRoomFindByDeleteYn(-1));
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    //모든 활성화된 방 리스트를 조회하는 서비스
    public ResponseEntity activeRoomList() {
        ChatResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	resultMap.put("chatRooms",chatRoomFindByDeleteYn(-1));
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    public ResponseEntity findByChannelId(Long channelId) {
        ChatResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	Optional<ChannelEntity> optChannelEntity = chatRoomFindByChannelId(channelId);
        	ChannelEntity channelEntity = optChannelEntity.orElseGet(() -> ChannelEntity.builder().build());
    
            resultMap.put("chatRoom", channelEntity);
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .message("요청 성공")
                    .result(resultMap).build();
            return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    //ChannelId를 통해 해당 방의 채팅 기록을 불러오는 서비스
    public ResponseEntity loadRoom(Long channelId) {
    	ChatResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
    	 	resultMap.put("chatArr", chatFindByChannelId(channelId));
    		chatResponse = ChatResponse.builder()
    				.statusCode(HttpStatus.OK.value())
    				.status(HttpStatus.OK)
    				.message("요청 성공")
    				.result(resultMap).build();
    		return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }	
    }
    
    //userCd와 ChannelId를 통해 해당 방의 유저를 퇴장시키는 서비스
    public ResponseEntity exitRoom(String userCd, Long channelId) {
    	ChatResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	if(chatRoomMemberEntityFindById(userCd, channelId).isPresent()) {
        		resultMap.put("chatRoomMemberEntity", chatRoomMemberRepository
        				.save(ChatRoomMemberEntity.builder().channelId(channelId).userCd(userCd).connectYn(-1).build()));
        		chatResponse = ChatResponse.builder()
        				.statusCode(HttpStatus.OK.value())
        				.status(HttpStatus.OK)
        				.message("요청 성공")
        				.result(resultMap).build();
        		return ResponseEntity.ok().body(chatResponse);
        	}else{
        		chatResponse = ChatResponse.builder()
        				.statusCode(HttpStatus.OK.value())
        				.status(HttpStatus.OK)
        				.message("해당 유저정보가 없습니다.")
        				.result(resultMap).build();
        		return ResponseEntity.ok().body(chatResponse);
        	}
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }	
    }
    
    //룸코드를 통해 각 방의 활성멤버들을 찾아 반환하는 서비스
    public ResponseEntity findActiveMember(Long channelId) {
    	ChatResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
    	 	resultMap.put("chatArr", chatRoomMemberFindByChannelIdAndConnectYn(channelId,1));
    		chatResponse = ChatResponse.builder()
    				.statusCode(HttpStatus.OK.value())
    				.status(HttpStatus.OK)
    				.message("요청 성공")
    				.result(resultMap).build();
    		return ResponseEntity.ok().body(chatResponse);
        } catch (Exception e) {
            e.printStackTrace();
            chatResponse = ChatResponse.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("서버쪽 오류가 발생했습니다. 관리자에게 문의하십시오")
                    .result(resultMap).build();
            return ResponseEntity.internalServerError().body(chatResponse);
        }
    }
    
    public void requestClient(String url, ChatEntity chatEntity) {
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
				.queryParam("userCd", chatEntity.getUserCd())
				.queryParam("toUserCd", chatEntity.getToUserCd()==null||chatEntity.getToUserCd().equals("") ? "" : chatEntity.getToUserCd())
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
    
    //[CRUD]chatRoom
    
    //채팅룸 변경 사항 저장하는 메서드
    public ChannelEntity chatRoomSave(ChannelEntity ChannelEntity) {
    	return chatRoomRepository.save(ChannelEntity);
    }
    //룸코드를 통해 채팅방을 조회하는 메서드 
    public Optional<ChannelEntity> chatRoomFindByChannelId(Long channelId){
    	return chatRoomRepository.findByChannelId(channelId);
    }
    //모든 채팅방을 조회하는 메서드
    public List<ChannelEntity> chatRoomFindAll(){
    	return chatRoomRepository.findAll();
    }
    //모든 활성 채팅방을 조회하는 메서드
    public List<ChannelEntity> chatRoomFindByDeleteYn(int deleteYn){
    	return chatRoomRepository.findByDeleteYn(deleteYn);
    }
    
    //[CRUD]chatRoomMember
    
    //채팅 저장하는 메서드
    public ChatRoomMemberEntity chatRoomMemberSave(ChatRoomMemberEntity chatRoomMemberEntity) {
    	return chatRoomMemberRepository.save(chatRoomMemberEntity);
    }
    //userCd와 ChannelId를 통해 해당하는 유저를 조회하는 메서드
    public Optional<ChatRoomMemberEntity> chatRoomMemberEntityFindById(String userCd, Long channelId) {
    	return chatRoomMemberRepository.findById(ChatRoomMemberId.builder().userCd(userCd).channelId(channelId).build());
    }
    //ChannelId를 통해 현재 방에 들어와있는 유저를 조회하는 메서드
    public List<ChatRoomMemberEntity> chatRoomMemberFindByChannelIdAndConnectYn(Long channelId, int connectYn) {
    	return chatRoomMemberRepository.findByChannelIdAndConnectYn(channelId, connectYn);
    }
    //ChannelId를 통해 현재 방에 들어와있는 유저를 조회하는 메서드
    public List<ChatRoomMemberEntity> chatRoomMemberFindByChannelIdAndUserCdAndConnectYn(Long channelId, String userCd, int connectYn) {
    	return chatRoomMemberRepository.findByChannelIdAndUserCdAndConnectYn(channelId, userCd, connectYn);
    }
    //session을 통해 유저를 조회하는 메서드	
    public Optional<ChatRoomMemberEntity> chatRoomMemberFindBySession(String session) {
    	return chatRoomMemberRepository.findBySession(session);
    }
    
    //[CRUD]chat
    
    //채팅 저장하는 메서드
    public ChatEntity chatSave(ChatEntity chatEntity) {
    	return chatRepository.save(chatEntity);
    }
    //ChannelId를 통해 해당 방의 채팅 기록을 불러오는 메서드
    public List<ChatEntity> chatFindByChannelId(Long channelId){
    	return chatRepository.findByChannelId(channelId);
    }
    
}