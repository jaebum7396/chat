package chat.model;

import java.io.Serializable;

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
	@Id
	@Column(nullable = false, name = "CHANNEL_ID")
	private Long channelId;
	
	@Id
	@Column(nullable = false, name = "USER_ID")
	private String userId;

    private String userNm;
    
    @Column(name = "SESSION_ID")
    private String sessionId;
    
    @Column(name = "CONNECT_YN")
    private int connectYn;
}