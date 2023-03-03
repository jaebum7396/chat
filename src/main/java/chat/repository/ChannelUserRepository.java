package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import chat.model.ChannelUserEntity;
import chat.model.ChannelUserId;

@Repository
public interface ChannelUserRepository extends JpaRepository<ChannelUserEntity, ChannelUserId> {
	List<ChannelUserEntity> findByChannelIdAndConnectYn(Long ChannelId, int ConnectYn);
	List<ChannelUserEntity> findByChannelIdAndUserIdAndConnectYn(Long ChannelId, String userId, int ConnectYn);
	Optional<ChannelUserEntity> findBySessionId(String session);
    List<ChannelUserEntity> findAll();
}
