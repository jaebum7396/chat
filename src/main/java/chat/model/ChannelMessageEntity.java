package chat.model;

import javax.persistence.Column; 
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "TB_CHANNEL_MESSAGE")
public class ChannelMessageEntity  extends BaseEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String MESSAGE_ID;
	
	//@Column(nullable = false)
    private String storeCd;
    
    private String messageType;
    
    @Column(nullable = false, name = "CHANNEL_ID")
    private Long channelId;

    @Column(nullable = false)
    private String userId;
    
    private String toUserId;

    private String message;
    
    private String domain;
}