//package findme.dangdangcrew.chat.controller;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
//import findme.dangdangcrew.chat.dto.ChatMessageRequestDto.MessageType;
//import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
//import findme.dangdangcrew.chat.service.ChatMessageService;
//import findme.dangdangcrew.chat.service.ChatRoomService;
//import findme.dangdangcrew.user.service.UserService;
//import java.lang.reflect.Type;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompFrameHandler;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class ChatControllerWebSocketTest {
//
//    @Autowired
//    private ChatRoomService chatRoomService; // 필요한 서비스 Mocking
//
//    @Autowired
//    private ChatMessageService chatMessageService;
//
//    @Autowired
//    private UserService userService;
//
//    private WebSocketStompClient stompClient;
//    private String wsUrl;
//
//    @BeforeEach
//    void setUp() {
//        wsUrl = "ws://localhost:8080/ws"; // WebSocket URL
//        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//    }
//
//    @Test
//    @DisplayName("웹소켓을 통하여 채팅 메시지를 전송한다.")
//    void sendChatMessage() throws ExecutionException, InterruptedException, TimeoutException {
//        CompletableFuture<ChatMessageResponseDto> future = new CompletableFuture<>();
//
//        Thread.sleep(10000);
//
//        // 동기적으로 WebSocket 세션 연결
//        StompSession session = stompClient.connectAsync(wsUrl, new StompSessionHandlerAdapter() {}).get(3, TimeUnit.SECONDS);
//
//        // 구독 설정
//        session.subscribe("/subscribe/chatroom.1", new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return ChatMessageResponseDto.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                try {
//                    future.complete((ChatMessageResponseDto) payload);
//                } catch (Exception e) {
//                    future.completeExceptionally(e);
//                }
//            }
//        });
//
//        // 메시지 전송
//        ChatMessageRequestDto requestDto = new ChatMessageRequestDto();
//        requestDto.setType(MessageType.TALK);
//        requestDto.setMessage("Hello WebSocket!");
//
//        session.send("/publish/chat.1", requestDto);
//
//        // 응답 확인 (타임아웃 5초)
//        ChatMessageResponseDto response = future.get(5, TimeUnit.SECONDS);
//
//        // 검증
//        assertThat(response.getMessage()).isEqualTo("Hello WebSocket!");
//    }
//}
