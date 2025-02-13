package findme.dangdangcrew.chat.service;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageRequestDto.MessageType;
import findme.dangdangcrew.chat.entity.ChatRoom;
import findme.dangdangcrew.chat.repository.ChatParticipantRepository;
import findme.dangdangcrew.chat.repository.ChatRoomRepository;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
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

    public String processChat(ChatMessageRequestDto chatMessage, Long roomId, Long userId) {
        if (chatMessage.getType() == ChatMessageRequestDto.MessageType.ENTER) {
            enterRoom(roomId, userId);
            return chatMessage.getSender() + "님이 입장하셨습니다.";
        }
        if (chatMessage.getType() == ChatMessageRequestDto.MessageType.LEAVE) {
            leaveRoom(roomId, userId);
            return chatMessage.getSender() + "님이 퇴장하셨습니다.";
        }
        chatMessageService.saveMessage(chatMessage, roomId);
        return chatMessage.getMessage();
    }

    public void enterRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        User user = userService.getUser(userId);
        checkIfAlreadyJoined(chatRoom, user);

        chatRoom.addParticipant();
    }

    public void leaveRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        User user = userService.getUser(userId);
        checkIfAlreadyLeft(chatRoom, user);

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
}
