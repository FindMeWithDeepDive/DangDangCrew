package findme.dangdangcrew.chat.repository;

import findme.dangdangcrew.chat.entity.ChatParticipant;
import findme.dangdangcrew.chat.entity.ChatRoom;
import findme.dangdangcrew.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    Optional<ChatParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    void deleteByChatRoomAndUser(ChatRoom chatRoom, User user);
}
