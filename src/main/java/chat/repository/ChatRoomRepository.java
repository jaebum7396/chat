package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import chat.model.ChatRoomEntity;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity,String> {
	ChatRoomEntity save(ChatRoomEntity chatRoomEntity);
    Optional<ChatRoomEntity> findByRoomCd(Long roomCd);
    List<ChatRoomEntity> findByDeleteYn(int deleteYn);
    List<ChatRoomEntity> findAll();
}
