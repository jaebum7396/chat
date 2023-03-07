package chat.model;

import java.time.LocalDateTime;

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
@Entity(name = "TB_MESSAGE")
public class MessageEntity  extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "MESSAGE_CD")
    private String messageCd;
	
	@Column(nullable = false, name = "CHANNEL_CD")
	private Long channelCd;

	@Column(nullable = false, name = "USER_CD") 
    private Long userCd;
	
	@Column(name = "SEND_TYPE")
	private String sendType;
	
	@Column(name = "MESSAGE_TYPE")
	private String messageType;

	@Column(name = "MESSAGE")
	private String message;

	@Column(name = "MESSAGE_TO")
	private Long messageTo;

	@Column(name = "MESSAGE_DT")
	private LocalDateTime messageDt;

	@Column(name = "UNREAD_COUNT")
    private int unreadCount;
	
	@Column(name = "LINK_MESSAGE_CD")
	private Long linkMessageCd;
}