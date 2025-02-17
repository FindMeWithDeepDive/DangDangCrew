package findme.dangdangcrew.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageRequestDto.MessageType;
import findme.dangdangcrew.chat.entity.ChatMessage;
import findme.dangdangcrew.chat.repository.ChatMessageRepository;
import findme.dangdangcrew.chat.service.ChatMessageService;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserService userService;

    private User user;
    private ChatMessage chatMessage;
    private ChatMessageRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .nickname("testUser")
                .phoneNumber("010-1234-5678")
                .build();

        requestDto = new ChatMessageRequestDto();
        requestDto.setType(MessageType.TALK);
        requestDto.setMessage("Hello, World!");

        chatMessage = ChatMessage.builder()
                .roomId(1L)
                .sender("testUser")
                .message("Hello, World!")
                .build();
    }

    @Test
    @DisplayName("메시지 저장에 성공한다.")
    void saveMessage_Success() {
        chatMessageService.saveMessage(requestDto, 1L, user);

        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("메시지 저장에 실패한다.(MessageType이 TALK가 아닌 경우)")
    void saveMessage_Fail() {
        requestDto.setType(MessageType.ENTER);

        chatMessageService.saveMessage(requestDto, 1L, user);

        verify(chatMessageRepository).save(any(ChatMessage.class));
    }
}
