package chat.model;

import java.net.URI;

import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    public enum MessageType {
        ENTER, COMM, EXIT
    }
    private MessageType messageType;
    
    @Column(nullable = false, name = "CHANNEL_ID")
    private Long channelId;
    
    private String userCd;

    private String toUserCd;
    
    private String message;
    
    private String domain;
    
    public ChatEntity toEntity(){
    	return ChatEntity.builder()
			.messageType(messageType.toString())
			.channelId(channelId)
			.userCd(userCd)
			.toUserCd(toUserCd)
			.message(message)
			.domain(domain)
			.build();
    }
    //public URI toUri() {
    //    return uriBuilder
    //    .queryParam("ChannelId", ChannelId)
    //    .queryParam("userCd", userCd)
    //    .queryParam("toUserCd", toUserCd)
    //    .queryParam("message", message)
    //    .build();
    //}
}
