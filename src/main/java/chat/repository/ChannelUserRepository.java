package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import chat.model.ChannelUserEntity;
import chat.model.ChannelUserId;

@Repository
public interface ChannelUserRepository extends JpaRepository<ChannelUserEntity, ChannelUserId> {
	List<ChannelUserEntity> findByChannelCdAndConnectYn(Long ChannelCd, char ConnectYn);
	List<ChannelUserEntity> findByChannelCdAndUserCdAndConnectYn(Long ChannelCd, Long userCd, char ConnectYn);
	Optional<ChannelUserEntity> findBySessionId(String session);
    List<ChannelUserEntity> findAll();
    
    @Query(value=
		"SELECT CHANNEL_CD " +
		/* ", group_concat(USER_CD) AS USER_CD_STR " + */   
    	"FROM TB_CHANNEL_USER tcu " + 
    	"GROUP BY CHANNEL_CD " + 
    	"HAVING COUNT(CHANNEL_CD) = 2 " +
    	"AND INSTR(group_concat(USER_CD), :LOGIN_USER_CD) " +
    	"AND INSTR(group_concat(USER_CD), :TO_USER_CD) " 
    	, nativeQuery = true
	)
    Object[] findExistingChannelCdWithUserCd(@Param("LOGIN_USER_CD") Long LOGIN_USER_CD, @Param("TO_USER_CD") Long TO_USER_CD);
}
