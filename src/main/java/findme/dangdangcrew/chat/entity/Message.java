package findme.dangdangcrew.chat.entity;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Document(collection = "chat_message")
public class Message {

    @Id
    private String id;

    private Long roomId;
    private String sender;
    private String message;
    private LocalDateTime time;

    public static Message create(Long roomId, String sender, String message) {
        return Message.builder()
            .roomId(roomId)
            .sender(sender)
            .message(message)
            .time(LocalDateTime.now())
            .build();
    }
}
