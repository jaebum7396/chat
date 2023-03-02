package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import chat.model.ChannelEntity;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChannelEntity,String> {
	ChannelEntity save(ChannelEntity ChannelEntity);
    Optional<ChannelEntity> findByChannelId(Long ChannelId);
    List<ChannelEntity> findByDeleteYn(int deleteYn);
    List<ChannelEntity> findAll();
}
