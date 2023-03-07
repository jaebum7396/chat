package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import chat.model.ChannelEntity;

@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity,String> {
	ChannelEntity save(ChannelEntity ChannelEntity);
    Optional<ChannelEntity> findByChannelCd(Long ChannelCd);
    List<ChannelEntity> findByDeleteYn(char deleteYn);
    List<ChannelEntity> findAll();
}
