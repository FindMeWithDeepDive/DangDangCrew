package findme.dangdangcrew.chat.repository;

import findme.dangdangcrew.chat.entity.ChatMessage;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByRoomIdOrderByTimeAsc(Long roomId);
}
