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
}