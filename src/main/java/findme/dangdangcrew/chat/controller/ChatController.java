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
            @DestinationVariable Long roomId,
            @Payload ChatMessageRequestDto chatMessage) {

        return new ChatMessageResponseDto(
                String.valueOf(roomId),
                chatMessage.getSender(),
                chatMessage.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @MessageMapping("/chat.enterRoom/{roomId}")
    @SendTo("/subscribe/chatroom/{roomId}")
    public ChatMessageResponseDto enterRoom(
            @DestinationVariable Long roomId,
            @Payload ChatMessageRequestDto chatMessage) {

        String welcomeMessage = chatMessage.getSender() + "님이 입장하셨습니다.";
        return new ChatMessageResponseDto(
                String.valueOf(roomId),
                "System", // 시스템 메시지
                welcomeMessage,
                LocalDateTime.now().format(formatter)
        );
    }

    @MessageMapping("/chat.leaveRoom/{roomId}")
    @SendTo("/subscribe/chatroom/{roomId}")
    public ChatMessageResponseDto leaveRoom(
            @DestinationVariable Long roomId,
            @Payload ChatMessageRequestDto chatMessage) {

        String leaveMessage = chatMessage.getSender() + "님이 퇴장하셨습니다.";
        return new ChatMessageResponseDto(
                String.valueOf(roomId),
                "System",
                leaveMessage,
                LocalDateTime.now().format(formatter)
        );
    }
}