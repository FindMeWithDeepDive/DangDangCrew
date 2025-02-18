package findme.dangdangcrew.chat.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import findme.dangdangcrew.chat.service.ChatMessageService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ChatHistoryControllerTest {

    private MockMvc mockMvc;
    private ChatMessageService chatMessageService;
    private ChatHistoryController chatHistoryController;

    @BeforeEach
    void setUp() {
        chatMessageService = Mockito.mock(ChatMessageService.class);
        chatHistoryController = new ChatHistoryController(chatMessageService);
        mockMvc = MockMvcBuilders.standaloneSetup(chatHistoryController).build();
    }

    @Test
    @DisplayName("채팅 내역 조회 성공")
    void getChatHistory_Success() throws Exception {
        Long roomId = 1L;
        List<ChatMessageResponseDto> chatMessages = List.of(
                new ChatMessageResponseDto("1", "user1", "Hello!", "2025-02-17 12:00:00"),
                new ChatMessageResponseDto("1", "user2", "Hi!", "2025-02-17 12:01:00")
        );

        when(chatMessageService.getMessages(roomId)).thenReturn(chatMessages);

        mockMvc.perform(get("/api/v1/chat/{roomId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].sender").value("user1"))
                .andExpect(jsonPath("$[0].message").value("Hello!"))
                .andExpect(jsonPath("$[1].sender").value("user2"))
                .andExpect(jsonPath("$[1].message").value("Hi!"))
                .andDo(print());
    }

    @Test
    @DisplayName("채팅 내역이 없는 경우 빈 리스트 반환")
    void getChatHistory_Empty() throws Exception {
        Long roomId = 2L;
        when(chatMessageService.getMessages(roomId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/chat/{roomId}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0))
                .andDo(print());
    }
}
