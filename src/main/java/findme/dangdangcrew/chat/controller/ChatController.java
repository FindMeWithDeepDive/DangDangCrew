package findme.dangdangcrew.chat.controller;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import findme.dangdangcrew.chat.service.ChatRoomService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat.{roomId}")
    @SendTo("/subscribe/chatroom.{roomId}")
    public ChatMessageResponseDto handleChat(
            @DestinationVariable Long roomId,
            @Payload ChatMessageRequestDto chatMessage) {

        Long userId = 1L; // 토큰 값에서 User 정보 추출해오는 로직이 없어서 에러가 생겨서 일단 임시로 넣어놓음.
        String message = chatRoomService.processChat(chatMessage, roomId, userId); // 추후 토큰 기반 유저 정보 추출이 개발되면 userId 파라미터 삭제 예정.

        return new ChatMessageResponseDto(
                String.valueOf(roomId),
                chatMessage.getSender(),
                message,
                LocalDateTime.now().format(formatter)
        );
    }
}