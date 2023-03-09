package chat.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "TB_CHANNEL")
public class ChannelEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "CHANNEL_CD")
    private Long channelCd;
   
    @Column(name = "CHANNEL_NAME")
    private String channelName;
    
    @Column(name = "CHANNEL_ORDER")
    private int channelOrder;
    
    @Column(name = "SESSION_ID")
    private String sessionId;
    
    @Column(name = "LAST_MESSAGE_CD")
    private Long lastMessageCd;
    
    @OneToMany(mappedBy = "channelEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL) @Builder.Default
	private List<ChannelUserEntity> channelUsers = new ArrayList<>();
    
    public void addChannelUser(ChannelUserEntity channelUser) {
    	this.channelUsers.add(channelUser);
    }
    public void setChannelUsers(List<ChannelUserEntity> channelUsers) {
        this.channelUsers = channelUsers;
        channelUsers.forEach(o -> o.setChannelEntity(this));
    }
}