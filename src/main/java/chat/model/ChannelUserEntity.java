package chat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
@IdClass(ChannelUserId.class)
@Entity(name = "TB_CHANNEL_USER")
public class ChannelUserEntity  extends BaseEntity implements Serializable{
	@Id @Column(nullable = false, name = "CHANNEL_CD")
	private Long channelCd;
	
	@Id @Column(nullable = false, name = "USER_CD")
	private Long userCd;

	@Column(name = "CHANNEL_ALIAS")
    private String channelAlias;
	
	@Column(name = "JOIN_DT")
    private LocalDateTime joinDt;
	
	@Column(name = "CONNECT_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
	private char connectYn;

	@Column(name = "SESSION_ID")
    private String sessionId;
	
	@Column(name = "CHANNEL_ORDER")
    private int channelOrder;
	
	@Column(name = "UNREAD_COUNT")
    private int unreadCount;
	
	@Column(name = "LAST_MESSAGE_CD")
	private Long lastMessageCd;
    
	@Column(name = "LAST_MESSAGE_DT")
    private LocalDateTime lastMessageDt;
}