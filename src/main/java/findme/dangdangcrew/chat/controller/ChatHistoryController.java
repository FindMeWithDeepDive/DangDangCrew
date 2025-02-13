package findme.dangdangcrew.chat.controller;

import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import findme.dangdangcrew.chat.service.ChatMessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatHistoryController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatHistory(@PathVariable Long roomId) {
        List<ChatMessageResponseDto> chatHistory = chatMessageService.getMessages(roomId);
        return ResponseEntity.ok(chatHistory);
    }
}
