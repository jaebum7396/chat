package chat.model;

import java.net.URI;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    public enum MessageType {
        ENTER, COMM, EXIT
    }
    private MessageType messageType;
    
    private Long roomCd;
    
    private String userCd;

    private String toUserCd;
    
    private String message;
    
    private String domain;
    
    public ChatEntity toEntity(){
    	return ChatEntity.builder()
			.messageType(messageType.toString())
			.roomCd(roomCd)
			.userCd(userCd)
			.toUserCd(toUserCd)
			.message(message)
			.domain(domain)
			.build();
    }
    //public URI toUri() {
    //    return uriBuilder
    //    .queryParam("roomCd", roomCd)
    //    .queryParam("userCd", userCd)
    //    .queryParam("toUserCd", toUserCd)
    //    .queryParam("message", message)
    //    .build();
    //}
}
