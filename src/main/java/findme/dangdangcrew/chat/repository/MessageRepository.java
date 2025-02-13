package findme.dangdangcrew.chat.repository;

import findme.dangdangcrew.chat.entity.Message;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByRoomIdOrderByTimeAsc(Long roomId);
}
