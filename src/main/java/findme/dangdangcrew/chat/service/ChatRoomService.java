package findme.dangdangcrew.chat.service;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageRequestDto.MessageType;
import findme.dangdangcrew.chat.entity.ChatRoom;
import findme.dangdangcrew.chat.repository.ChatRoomRepository;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.meeting.entity.Meeting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;

    public String processChat(ChatMessageRequestDto chatMessage, Long roomId) {
        if (chatMessage.getType() == ChatMessageRequestDto.MessageType.ENTER) {
            enterRoom(roomId);
            return chatMessage.getSender() + "님이 입장하셨습니다.";
        }
        if (chatMessage.getType() == ChatMessageRequestDto.MessageType.LEAVE) {
            leaveRoom(roomId);
            return chatMessage.getSender() + "님이 퇴장하셨습니다.";
        }
        chatMessageService.saveMessage(chatMessage, roomId);
        return chatMessage.getMessage();
    }

    public void enterRoom(Long roomId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        if (!chatRoom.addParticipant()) {
            throw new CustomException(ErrorCode.CHAT_ROOM_FULL);
        }
    }

    public void leaveRoom(Long roomId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        chatRoom.removeParticipant();
    }

    private ChatRoom getChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return chatRoom;
    }

    public void createChatRoom(Meeting meeting) {
        chatRoomRepository.save(new ChatRoom(meeting));
    }
}
