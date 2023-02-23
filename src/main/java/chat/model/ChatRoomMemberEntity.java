package chat.model;

import java.io.Serializable;

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
@IdClass(ChatRoomMemberId.class)
@Entity(name = "TB_CHAT_ROOM_MEMBER")
public class ChatRoomMemberEntity  extends BaseEntity implements Serializable{
	@Id
	private String userCd;
	@Id
	private Long roomCd;

    private String userNm;
    
    private String session;
    
    private int connectYn;
}