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
@Entity(name = "TB_MESSAGE_READ")
public class MessageReadEntity  extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name = "READ_CD")
    private Long readCd;
    
    @Column(name = "MESSAGE_CD")
    private Long messageCd;
    
    @Column(name = "USER_CD")
    private Long userCd;
    
    @Column(name = "READ_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private char readYn;
    
}