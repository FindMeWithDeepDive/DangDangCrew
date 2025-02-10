package findme.dangdangcrew.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessageResponseDto {
    private String roomId;
    private String sender;
    private String message;
    private String timestamp;
}
