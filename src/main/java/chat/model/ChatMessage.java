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
    
    private String userId;

    private String toUserId;
    
    private String message;
    
    private String domain;
    
    public ChannelMessageEntity toEntity(){
    	return ChannelMessageEntity.builder()
			.messageType(messageType.toString())
			.channelId(channelId)
			.userId(userId)
			.toUserId(toUserId)
			.message(message)
			.domain(domain)
			.build();
    }
    //public URI toUri() {
    //    return uriBuilder
    //    .queryParam("ChannelId", ChannelId)
    //    .queryParam("userId", userId)
    //    .queryParam("touserId", touserId)
    //    .queryParam("message", message)
    //    .build();
    //}
}
