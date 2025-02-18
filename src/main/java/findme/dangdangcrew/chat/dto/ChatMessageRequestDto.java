package findme.dangdangcrew.chat.dto;

import lombok.Data;

@Data
public class ChatMessageRequestDto {
    public enum MessageType {
        ENTER, TALK, LEAVE
    }

    private MessageType type;
    private String message;
}
