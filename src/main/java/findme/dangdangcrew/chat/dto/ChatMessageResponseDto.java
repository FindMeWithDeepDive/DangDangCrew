package findme.dangdangcrew.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private String roomId;
    private String sender;
    private String message;
    private String timestamp;
}
