package findme.dangdangcrew.chat.controller;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/subscribe/chatroom/{roomId}")
    public ChatMessageResponseDto sendMessage(
            @DestinationVariable String roomId,
            @Payload ChatMessageRequestDto chatMessage) {

        return new ChatMessageResponseDto(
                String.valueOf(roomId),
                chatMessage.getSender(),
                chatMessage.getMessage(),
                LocalDateTime.now().format(formatter) // 현재 시간 추가
        );
    }
}