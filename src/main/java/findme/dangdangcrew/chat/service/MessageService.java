package findme.dangdangcrew.chat.service;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import findme.dangdangcrew.chat.entity.ChatMessage;
import findme.dangdangcrew.chat.repository.ChatMessageRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
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

    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessages(Long roomId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderByTimeAsc(roomId);
        return chatMessages.stream()
                .map(m -> new ChatMessageResponseDto(
                        String.valueOf(roomId),
                        m.getSender(),
                        m.getMessage(),
                        m.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                ))
                .collect(Collectors.toList());
    }
}
