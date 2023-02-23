package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import chat.model.ChatEntity;
import chat.model.ChatRoomEntity;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity,String> {
    List<ChatEntity> findAll();
    List<ChatEntity> findByRoomCd(Long roomCd);
}
