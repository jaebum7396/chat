package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import chat.model.ChannelEntity;
import chat.model.ChannelUserEntity;
import chat.model.ChannelUserId;

@Repository
public interface ChannelUserRepository extends JpaRepository<ChannelUserEntity, ChannelUserId> {
	List<ChannelUserEntity> findByChannelEntity(ChannelEntity channelEntity);
	List<ChannelUserEntity> findByChannelEntityAndUserCd(ChannelEntity channelEntity, Long userCd);
	List<ChannelUserEntity> findByChannelEntityAndConnectYn(ChannelEntity channelEntity, char ConnectYn);
	List<ChannelUserEntity> findByChannelEntityAndUserCdAndConnectYn(ChannelEntity channelEntity, Long userCd, char ConnectYn);
	
	List<ChannelUserEntity> findByUserCd(Long UserCd);
	Optional<ChannelUserEntity> findBySessionId(String session);
    List<ChannelUserEntity> findAll();
    
    @Query(value=
		"SELECT CHANNEL_CD " +
		/* ", group_concat(USER_CD) AS USER_CD_STR " + */   
    	"FROM TB_CHANNEL_USER tcu " + 
    	"GROUP BY CHANNEL_CD " + 
    	"HAVING COUNT(CHANNEL_CD) = 2 " +
    	"AND INSTR(group_concat(USER_CD), :loginUserCd) " +
    	"AND INSTR(group_concat(USER_CD), :toUserCd) " 
    	, nativeQuery = true
	)
    Object[] findExistingChannelCdWithUserCd(@Param("loginUserCd") Long loginUserCd, @Param("toUserCd") Long toUserCd);
}
