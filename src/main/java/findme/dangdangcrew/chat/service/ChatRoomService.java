package findme.dangdangcrew.chat.service;

import findme.dangdangcrew.chat.entity.ChatRoom;
import findme.dangdangcrew.chat.repository.ChatRoomRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public void enterRoom(Long roomId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        if (!chatRoom.addParticipant()) {
            throw new IllegalStateException("채팅방이 가득 찼습니다. (최대 20명)");
        }
    }

    private ChatRoom getChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        return chatRoom;
    }

}
