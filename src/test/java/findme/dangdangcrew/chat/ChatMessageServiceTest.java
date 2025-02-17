package findme.dangdangcrew.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageRequestDto.MessageType;
import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import findme.dangdangcrew.chat.entity.ChatMessage;
import findme.dangdangcrew.chat.repository.ChatMessageRepository;
import findme.dangdangcrew.chat.service.ChatMessageService;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import java.util.List;
import net.bytebuddy.build.ToStringPlugin.Enhance;
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

    @Test
    @DisplayName("채팅 메시지 불러오기에 성공한다.")
    void getMessages_Success() {
        List<ChatMessage> messages = List.of(
                ChatMessage.create(1L, "user1", "첫 번째 메시지"),
                ChatMessage.create(1L, "user2", "두 번째 메시지")
        );

        when(chatMessageRepository.findByRoomIdOrderByTimeAsc(1L)).thenReturn(messages);

        List<ChatMessageResponseDto> result = chatMessageService.getMessages(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSender()).isEqualTo("user1");
        assertThat(result.get(0).getMessage()).isEqualTo("첫 번째 메시지");
        assertThat(result.get(1).getSender()).isEqualTo("user2");
        assertThat(result.get(1).getMessage()).isEqualTo("두 번째 메시지");
    }

    @Test
    @DisplayName("채팅 메시지 볼러오기에 성공한다.(메시지가 없는 경우)")
    void getMessages_Success_NoMessages() {
        List<ChatMessage> messages = List.of();

        when(chatMessageRepository.findByRoomIdOrderByTimeAsc(1L)).thenReturn(messages);

        List<ChatMessageResponseDto> result = chatMessageService.getMessages(1L);

        assertThat(result).isEmpty();
    }
}
