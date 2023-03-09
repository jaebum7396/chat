package chat.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@IdClass(ChannelUserId.class)
@Entity(name = "TB_CHANNEL_USER")
public class ChannelUserEntity extends BaseEntity implements Serializable{
	
	@Id @ManyToOne @JoinColumn(name = "CHANNEL_CD")
	private ChannelEntity channelEntity;
	
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
	
	public void setChannelEntity(ChannelEntity channelEntity) {
        this.channelEntity = channelEntity;
    }
}