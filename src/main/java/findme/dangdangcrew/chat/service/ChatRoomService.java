package findme.dangdangcrew.chat.service;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.entity.ChatRoom;
import findme.dangdangcrew.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public String processChat(ChatMessageRequestDto chatMessage, Long roomId) {
        if (chatMessage.getType() == ChatMessageRequestDto.MessageType.ENTER) {
            enterRoom(roomId);
            return chatMessage.getSender() + "님이 입장하셨습니다.";
        }
        if (chatMessage.getType() == ChatMessageRequestDto.MessageType.LEAVE) {
            leaveRoom(roomId);
            return chatMessage.getSender() + "님이 퇴장하셨습니다.";
        }
        return chatMessage.getMessage();
    }

    public void enterRoom(Long roomId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        if (!chatRoom.addParticipant()) {
            throw new IllegalStateException("채팅방이 가득 찼습니다. (최대 20명)");
        }
    }

    public void leaveRoom(Long roomId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        chatRoom.removeParticipant();
    }

    private ChatRoom getChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        return chatRoom;
    }
}
