package findme.dangdangcrew.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import findme.dangdangcrew.chat.entity.ChatParticipant;
import findme.dangdangcrew.chat.entity.ChatRoom;
import findme.dangdangcrew.chat.repository.ChatParticipantRepository;
import findme.dangdangcrew.chat.repository.ChatRoomRepository;
import findme.dangdangcrew.chat.service.ChatRoomService;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    private User user;
    private ChatRoom chatRoom;
    private Meeting meeting;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .nickname("testUser")
                .phoneNumber("010-1234-5678")
                .build();

        meeting = Meeting.builder()
                .meetingName("Test Meeting")
                .information("Meeting Information")
                .maxPeople(10)
                .curPeople(1)
                .status(MeetingStatus.IN_PROGRESS)
                .build();

        chatRoom = new ChatRoom(meeting);
    }

    @Test
    @DisplayName("채팅방에 입장에 성공한다.")
    void enterChatRoom_Success() {
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(chatParticipantRepository.findByChatRoomAndUser(chatRoom, user)).thenReturn(Optional.empty());

        chatRoomService.enterRoom(1L, user);

        assertThat(chatRoom.getCurrentParticipants()).isEqualTo(2);
    }

    @Test
    @DisplayName("채팅방에 이미 입장한 경우 입장에 실패한다.")
    void enterChatRoom_Fail() {
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(chatParticipantRepository.findByChatRoomAndUser(chatRoom, user)).thenReturn(
                Optional.of(ChatParticipant.builder()
                        .chatRoom(chatRoom)
                        .user(user)
                        .build()));

        assertThatThrownBy(() -> chatRoomService.enterRoom(1L, user))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_JOINED.getMessage());
    }

    @Test
    @DisplayName("채팅방에 퇴장에 성공한다.")
    void leaveChatRoom_Success() {
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(chatParticipantRepository.findByChatRoomAndUser(any(ChatRoom.class), any(User.class)))
                .thenReturn(Optional.of(new ChatParticipant(chatRoom, user)));

        chatRoomService.leaveRoom(1L, user);

        verify(chatParticipantRepository, times(1)).deleteByChatRoomAndUser(chatRoom, user);
    }

    @Test
    @DisplayName("채팅방에서 이미 퇴장한 경우 퇴장에 실패한다.")
    void leaveChatRoom_Fail() {
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(chatParticipantRepository.findByChatRoomAndUser(any(ChatRoom.class), any(User.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatRoomService.leaveRoom(1L, user))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_LEFT.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 채팅방에는 입장에 실패한다.")
    void enterChatRoom_NotExist() {
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatRoomService.enterRoom(1L, user))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CHAT_ROOM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 채팅방에서 퇴장에 실패한다.")
    void leaveChatRoom_NotExist() {
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatRoomService.leaveRoom(1L, user))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CHAT_ROOM_NOT_FOUND.getMessage());
    }
}
