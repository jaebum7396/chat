package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import chat.model.ChannelMessageEntity;
import chat.model.ChannelEntity;

@Repository
public interface ChannelMessageRepository extends JpaRepository<ChannelMessageEntity,String> {
    List<ChannelMessageEntity> findAll();
    List<ChannelMessageEntity> findByChannelId(Long ChannelId);
}
