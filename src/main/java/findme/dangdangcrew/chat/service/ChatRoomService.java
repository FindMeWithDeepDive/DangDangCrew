package findme.dangdangcrew.chat.service;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import findme.dangdangcrew.chat.entity.ChatParticipant;
import findme.dangdangcrew.chat.entity.ChatRoom;
import findme.dangdangcrew.chat.repository.ChatParticipantRepository;
import findme.dangdangcrew.chat.repository.ChatRoomRepository;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final ChatParticipantRepository chatParticipantRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ChatMessageResponseDto processChat(ChatMessageRequestDto chatMessage, Long roomId,
                                              Map<String, Object> sessionAttributes) {
        Long userId = (Long) sessionAttributes.get("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.NULL_POINT);
        }
        User user = userService.getUser(userId);

        if (chatMessage.getType() == ChatMessageRequestDto.MessageType.ENTER) {
            enterRoom(roomId, user);
            return toResponse(roomId, user, user.getNickname() + "님이 입장하셨습니다.");
        }
        if (chatMessage.getType() == ChatMessageRequestDto.MessageType.LEAVE) {
            leaveRoom(roomId, user);
            return toResponse(roomId, user, user.getNickname() + "님이 퇴장하셨습니다.");
        }
        chatMessageService.saveMessage(chatMessage, roomId, user);
        return toResponse(roomId, user, chatMessage.getMessage());
    }

    private ChatMessageResponseDto toResponse(Long roomId, User user, String message) {
        return ChatMessageResponseDto.builder()
            .roomId(String.valueOf(roomId))
            .sender(user.getNickname())
            .message(message)
            .timestamp(LocalDateTime.now().format(formatter))
            .build();
    }

    public void enterRoom(Long roomId, User user) {
        ChatRoom chatRoom = getChatRoom(roomId);
        checkIfAlreadyJoined(chatRoom, user);

        saveChatParticipant(chatRoom, user);
        chatRoom.addParticipant();
    }

    public void leaveRoom(Long roomId, User user) {
        ChatRoom chatRoom = getChatRoom(roomId);
        checkIfAlreadyLeft(chatRoom, user);

        chatParticipantRepository.deleteByChatRoomAndUser(chatRoom, user);
        chatRoom.removeParticipant();
    }

    private ChatRoom getChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return chatRoom;
    }

    public void checkIfAlreadyJoined(ChatRoom chatRoom, User user) {
        if (chatParticipantRepository.findByChatRoomAndUser(chatRoom, user).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_JOINED);
        }
    }

    public void checkIfAlreadyLeft(ChatRoom chatRoom, User user) {
        if (chatParticipantRepository.findByChatRoomAndUser(chatRoom, user).isEmpty()) {
            throw new CustomException(ErrorCode.ALREADY_LEFT);
        }
    }

    public void createChatRoom(Meeting meeting) {
        chatRoomRepository.save(new ChatRoom(meeting));
    }

    private void saveChatParticipant(ChatRoom chatRoom, User user) {
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }
}
