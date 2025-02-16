package findme.dangdangcrew.chat.controller;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import findme.dangdangcrew.chat.service.ChatRoomService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat.{roomId}")
    @SendTo("/subscribe/chatroom.{roomId}")
    public ChatMessageResponseDto handleChat(
            @DestinationVariable Long roomId,
            @Payload ChatMessageRequestDto chatMessage,
            @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {

        return chatRoomService.processChat(chatMessage, roomId, sessionAttributes);
    }
}