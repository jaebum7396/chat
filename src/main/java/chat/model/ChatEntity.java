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
@Entity(name = "TB_CHAT")
public class ChatEntity  extends BaseEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String chatCd;
	
	//@Column(nullable = false)
    private String storeCd;
    
    @Column(nullable = false)
    private String userCd;
    
    private String toUserCd;

    private String message;
    
    private String messageType;
    
    @Column(nullable = false)
    private Long roomCd;
}