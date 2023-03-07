package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import chat.model.MessageEntity;
import chat.model.ChannelEntity;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity,String> {
    List<MessageEntity> findAll();
    List<MessageEntity> findByChannelCd(Long ChannelCd);
}
