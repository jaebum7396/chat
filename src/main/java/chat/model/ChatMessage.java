package chat.model;

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
    
    private String toUserCd;
    
    private String userCd;
    
    private String message;
}
