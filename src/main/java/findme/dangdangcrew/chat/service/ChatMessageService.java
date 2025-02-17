package findme.dangdangcrew.chat.service;

import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
import findme.dangdangcrew.chat.entity.ChatMessage;
import findme.dangdangcrew.chat.repository.ChatMessageRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void saveMessage(ChatMessageRequestDto chatMessageRequestDto, Long roomId, User user) {
        ChatMessage chatMessage = ChatMessage.create(roomId, user.getNickname(), chatMessageRequestDto.getMessage());
        chatMessageRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessages(Long roomId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderByTimeAsc(roomId);
        return chatMessages.stream()
            .map(m -> ChatMessageResponseDto.builder()
                .roomId(String.valueOf(roomId))
                .sender(m.getSender())
                .message(m.getMessage())
                .timestamp(m.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build())
            .collect(Collectors.toList());
    }
}
