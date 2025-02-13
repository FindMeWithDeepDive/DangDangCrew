package findme.dangdangcrew.chat.service;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.entity.ChatMessage;
import findme.dangdangcrew.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void saveMessage(ChatMessageRequestDto chatMessageRequestDto, Long roomId) {
        ChatMessage chatMessage = ChatMessage.create(roomId, chatMessageRequestDto.getSender(), chatMessageRequestDto.getMessage());
        chatMessageRepository.save(chatMessage);
    }
}
