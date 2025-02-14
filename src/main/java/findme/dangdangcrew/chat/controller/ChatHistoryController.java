package findme.dangdangcrew.chat.controller;

import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import findme.dangdangcrew.chat.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "[Chat] Chat API", description = "채팅 관련 API")
public class ChatHistoryController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{roomId}")
    @Operation(summary = "채팅 내역 불러오기", description = "이전 채팅 내역을 불러옵니다.")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatHistory(@PathVariable Long roomId) {
        List<ChatMessageResponseDto> chatHistory = chatMessageService.getMessages(roomId);
        return ResponseEntity.ok(chatHistory);
    }
}
