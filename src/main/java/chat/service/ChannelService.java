package chat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import chat.model.ChannelEntity;
import chat.model.ChannelResponse;
import chat.model.ChannelUserEntity;
import chat.model.MessageEntity;
import chat.repository.ChannelRepository;
import chat.repository.ChannelUserRepository;
import chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelService {
    private final ObjectMapper objectMapper;
    private ArrayList<ChannelEntity> ChannelEntityArr;
    private Set<WebSocketSession> sessions = new HashSet<>();
    
    @Autowired ChannelRepository channelRepository;
    @Autowired ChannelUserRepository channelUserRepository;
    @Autowired MessageRepository messageRepository;

    @PostConstruct
    private void init() {
    	ChannelEntityArr = new ArrayList<ChannelEntity>();
    }
    
    //웹소켓 요청이 왔을때 처리되는 서비스
    //public void handlerActions(WebSocketSession session, MessageEntity messageEntity) {
	public void handlerActions(WebSocketSession session, char connectYn) {
		if('Y' == connectYn) {
			sessions.add(session);
		}else {
			sessions.remove(session);
		}
		log.info("전체세션 : " +sessions.toString());
    }
    
    //메시지 전송 처리 서비스
    public <T> ResponseEntity sendMessage(MessageEntity messageEntity) {
    	System.out.println(messageEntity.toString());
    	ChannelResponse chatResponse;
    	Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
    	log.info("전체세션 : " +sessions.toString());
    	
        sessions.parallelStream()
        .forEach(session -> {
        	List<ChannelUserEntity> crme = new ArrayList<ChannelUserEntity>();
        	if(!"".equals(messageEntity.getMessageTo())&&messageEntity.getMessageTo()!=null){
        		System.out.println("touserCd != null");
        		crme = channelUserFindByChannelCdAndUserCd(ChannelEntity.builder().channelCd(messageEntity.getChannelCd()).build(), messageEntity.getMessageTo());
        	}else {
        		System.out.println("touserCd == null");
        		crme = channelUserFindByChannelCd(ChannelEntity.builder().channelCd(messageEntity.getChannelCd()).build());
        	}
        	
    		try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageEntity)));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        });
        //채팅 로그를 저장한다.
        MessageEntity chatEntity = messageRepository.save(messageEntity);
        
        resultMap.put("messageEntity",messageEntity);
        
        chatResponse = ChannelResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("요청 성공")
                .result(resultMap).build();
        return ResponseEntity.ok().body(chatResponse);
    }

    //방을 생성하는 서비스
    public ResponseEntity createChannel(String channelName) {
        ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
            String randomId = UUID.randomUUID().toString();
            ChannelEntity channelEntity = ChannelEntity.builder().channelName(channelName).deleteYn('N').build();
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
    public ResponseEntity createChannelWithUser(Long loginUserCd, Long toUserCd) {
        ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	String randomId = UUID.randomUUID().toString();
        	ChannelEntity channelEntity;
        	
        	Object[] channelCdArr = channelUserRepository.findExistingChannelCdWithUserCd(loginUserCd, toUserCd);
        	if(channelCdArr.length==0) {
        		System.out.println("신규 개설 채널입니다 ");
        		channelEntity = ChannelEntity.builder().deleteYn('N').build();
        		
        	}else {
        		System.out.println("존재하는 채널 코드입니다 : "+ Long.parseLong(channelCdArr[0].toString()));
        		channelEntity = ChannelEntity.builder().channelCd(Long.parseLong(channelCdArr[0].toString())).deleteYn('N').build();
        	}
        	
            ChannelEntityArr.add(channelEntity);  
            channelEntity.addChannelUser(ChannelUserEntity.builder().channelEntity(channelEntity).userCd(loginUserCd).build());
            channelEntity.addChannelUser(ChannelUserEntity.builder().channelEntity(channelEntity).userCd(toUserCd).build());
            channelEntity = channelSave(channelEntity);
            
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
    
    //모든 활성화된 방 리스트를 조회하는 서비스
    public ResponseEntity activeMyChannelList(Long loginUserCd) {
        ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	resultMap.put("channels", channelUserRepository.findByUserCd(loginUserCd));
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
        	resultMap.put("channels", channelFindByDeleteYn('N'));
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
    
    public ResponseEntity findByChannelCd(Long channelCd) {
        ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
        	Optional<ChannelEntity> optChannelEntity = channelFindByChannelCd(channelCd);
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
    
    //ChannelCd를 통해 해당 방의 채팅 기록을 불러오는 서비스
    public ResponseEntity loadChannel(Long channelCd) {
    	ChannelResponse chatResponse;
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
    	 	resultMap.put("chatArr", chatFindByChannelCd(channelCd));
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
    
    public void requestClient(String url, MessageEntity chatEntity) {
    	try {
    		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(url);
    		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
    		
    		WebClient webClient = WebClient.builder()
    				.uriBuilderFactory(factory)
    				.baseUrl(url)
    				.build();
    		
    		String uri = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("MESSAGE_CD", chatEntity.getMessageCd())
    			.queryParam("ChannelCd", chatEntity.getChannelCd())
				.queryParam("userCd", chatEntity.getUserCd())
				.queryParam("touserCd", chatEntity.getMessageTo()==null||chatEntity.getMessageTo().equals("") ? "" : chatEntity.getMessageTo())
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
    public Optional<ChannelEntity> channelFindByChannelCd(Long channelCd){
    	return channelRepository.findByChannelCd(channelCd);
    }
    //모든 채팅방을 조회하는 메서드
    public List<ChannelEntity> channelFindAll(){
    	return channelRepository.findAll();
    }
    //모든 활성 채팅방을 조회하는 메서드
    public List<ChannelEntity> channelFindByDeleteYn(char deleteYn){
    	return channelRepository.findByDeleteYn(deleteYn);
    }
    //모든 활성 채팅방을 조회하는 메서드
    //public List<ChannelEntity> channelFindByUserCdAndDeleteYn(Long userCd, char deleteYn){
    //	return channelRepository.findByUserCdAndDeleteYn(userCd, deleteYn);
    //}
    
    //[CRUD]channelMember
    
    //채팅 저장하는 메서드
    public ChannelUserEntity channelUserSave(ChannelUserEntity channelUserEntity) {
    	return channelUserRepository.save(channelUserEntity);
    }
    //ChannelCd를 통해 현재 방에 들어와있는 유저를 조회하는 메서드
    public List<ChannelUserEntity> channelUserFindByChannelCd(ChannelEntity channelCd) {
    	return channelUserRepository.findByChannelEntity(channelCd);
    }
    //ChannelCd를 통해 현재 방에 들어와있는 유저를 조회하는 메서드
    public List<ChannelUserEntity> channelUserFindByChannelCdAndUserCd(ChannelEntity channelCd, Long userCd) {
    	return channelUserRepository.findByChannelEntityAndUserCd(channelCd, userCd);
    }
    //ChannelCd를 통해 현재 방에 들어와있는 유저를 조회하는 메서드
    public List<ChannelUserEntity> channelUserFindByChannelCdAndConnectYn(ChannelEntity channelCd, char connectYn) {
    	return channelUserRepository.findByChannelEntityAndConnectYn(channelCd, connectYn);
    }
    //ChannelCd를 통해 현재 방에 들어와있는 유저를 조회하는 메서드
    public List<ChannelUserEntity> channelUserFindByChannelCdAndUserCdAndConnectYn(ChannelEntity channelCd, Long userCd, char connectYn) {
    	return channelUserRepository.findByChannelEntityAndUserCdAndConnectYn(channelCd, userCd, connectYn);
    }
    //session을 통해 유저를 조회하는 메서드	
    public Optional<ChannelUserEntity> channelUserFindBySession(String session) {
    	return channelUserRepository.findBySessionId(session);
    }
    
    //[CRUD]chat
    
    //채팅 저장하는 메서드
    public MessageEntity chatSave(MessageEntity chatEntity) {
    	return messageRepository.save(chatEntity);
    }
    //ChannelCd를 통해 해당 방의 채팅 기록을 불러오는 메서드
    public List<MessageEntity> chatFindByChannelCd(Long channelCd){
    	return messageRepository.findByChannelCd(channelCd);
    }
    
}