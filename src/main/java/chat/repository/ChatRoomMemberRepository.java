package chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import chat.model.ChatRoomMemberEntity;
import chat.model.ChatRoomMemberId;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMemberEntity, ChatRoomMemberId> {
	List<ChatRoomMemberEntity> findByRoomCdAndConnectYn(Long roomCd, int ConnectYn);
	List<ChatRoomMemberEntity> findByRoomCdAndUserCdAndConnectYn(Long roomCd, String userCd, int ConnectYn);
	Optional<ChatRoomMemberEntity> findBySession(String session);
    List<ChatRoomMemberEntity> findAll();
}