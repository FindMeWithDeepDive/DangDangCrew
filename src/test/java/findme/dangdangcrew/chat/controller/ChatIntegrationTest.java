//package findme.dangdangcrew.chat.controller;
//
//import findme.dangdangcrew.chat.dto.ChatMessageRequestDto;
//import findme.dangdangcrew.chat.dto.ChatMessageResponseDto;
//import findme.dangdangcrew.chat.entity.ChatRoom;
//import findme.dangdangcrew.chat.repository.ChatRoomRepository;
//import findme.dangdangcrew.meeting.entity.Meeting;
//import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
//import findme.dangdangcrew.user.entity.User;
//import findme.dangdangcrew.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.stomp.StompFrameHandler;
//import org.springframework.messaging.simp.stomp.StompHeaders;
//import org.springframework.messaging.simp.stomp.StompSession;
//import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.sockjs.client.Transport;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class ChatIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ChatRoomRepository chatRoomRepository;
//
//    private WebSocketStompClient stompClient;
//    private StompSession stompSession;
//    private String wsUrl;
//    private User testUser1;
//    private User testUser2;
//    private ChatRoom chatRoom;
//    private Long roomId;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        List<Transport> transports = new ArrayList<>();
//        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
//        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
//        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//        this.wsUrl = "ws://localhost:" + port + "/ws";
//
//        testUser1 = User.builder()
//                .email("test@example.com")
//                .password("password")
//                .name("Test User1")
//                .nickname("testUser1")
//                .phoneNumber("010-1234-5678")
//                .build();
//        testUser2 = User.builder()
//                .email("test2@example.com")
//                .password("password")
//                .name("Test User2")
//                .nickname("testUser2")
//                .phoneNumber("010-1234-5679")
//                .build();
//        setFieldWithReflection(testUser1, "id", 1L);
//        setFieldWithReflection(testUser2, "id", 2L);
//        userRepository.save(testUser1);
//        userRepository.save(testUser2);
//
//        Meeting meeting = createTestMeeting(1L);
//        chatRoom = new ChatRoom(meeting);
//        setFieldWithReflection(chatRoom, "id", 1L);
//        chatRoomRepository.save(chatRoom);
//        roomId = chatRoom.getId();
//
//        this.stompSession = stompClient.connectAsync(
//                wsUrl,
//                new StompSessionHandlerAdapter() {}
//        ).get(5, TimeUnit.SECONDS);
//    }
//
//    @Test
//    @DisplayName("채팅 메시지 전송 및 수신을 성공합니다.")
//    void sendAndReceiveChatMessage_Success() throws Exception {
//        CompletableFuture<ChatMessageResponseDto> completableFuture = new CompletableFuture<>();
//
//        stompSession.subscribe("/topic/chat/" + roomId, new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return ChatMessageResponseDto.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                completableFuture.complete((ChatMessageResponseDto) payload);
//            }
//        });
//
//        StompHeaders headers = new StompHeaders();
//        headers.add("Authorization", "Bearer test-token");
//
//        ChatMessageRequestDto requestDto = new ChatMessageRequestDto();
//        requestDto.setType(ChatMessageRequestDto.MessageType.TALK);
//        requestDto.setMessage("안녕하세요, 테스트 메시지입니다.");
//
//        stompSession.send("/publish/chat/" + roomId, requestDto);
//
//        ChatMessageResponseDto response = completableFuture.get(5, TimeUnit.SECONDS);
//        assertThat(response).isNotNull();
//        assertThat(response.getRoomId()).isEqualTo(roomId.toString());
//        assertThat(response.getMessage()).isEqualTo("안녕하세요, 테스트 메시지입니다.");
//        assertThat(response.getSender()).isEqualTo(testUser1.getNickname());
//    }
//
//    @Test
//    @DisplayName("채팅방 입장에 성공합니다.")
//    void enterChatRoom_Success() throws Exception {
//        CompletableFuture<ChatMessageResponseDto> completableFuture = new CompletableFuture<>();
//
//        stompSession.subscribe("/topic/chat/" + roomId, new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return ChatMessageResponseDto.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                completableFuture.complete((ChatMessageResponseDto) payload);
//            }
//        });
//
//        StompHeaders headers = new StompHeaders();
//        headers.add("Authorization", "Bearer test-token");
//
//        ChatMessageRequestDto requestDto = new ChatMessageRequestDto();
//        requestDto.setType(ChatMessageRequestDto.MessageType.ENTER);
//        requestDto.setMessage("");
//
//        stompSession.send("/publish/chat/" + roomId, requestDto);
//
//        ChatMessageResponseDto response = completableFuture.get(5, TimeUnit.SECONDS);
//        assertThat(response).isNotNull();
//        assertThat(response.getRoomId()).isEqualTo(roomId.toString());
//        assertThat(response.getMessage()).contains("입장");
//    }
//
//    @Test
//    @DisplayName("채팅방 퇴장에 성공합니다.")
//    void leaveChatRoom_Success() throws Exception {
//        // Given
//        CompletableFuture<ChatMessageResponseDto> completableFuture = new CompletableFuture<>();
//
//        stompSession.subscribe("/topic/chat/" + roomId, new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return ChatMessageResponseDto.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                completableFuture.complete((ChatMessageResponseDto) payload);
//            }
//        });
//
//        ChatMessageRequestDto requestDto = new ChatMessageRequestDto();
//        requestDto.setType(ChatMessageRequestDto.MessageType.LEAVE);
//        requestDto.setMessage("");
//
//        stompSession.send("/publish/chat/" + roomId, requestDto);
//
//        ChatMessageResponseDto response = completableFuture.get(5, TimeUnit.SECONDS);
//        assertThat(response).isNotNull();
//        assertThat(response.getRoomId()).isEqualTo(roomId.toString());
//        assertThat(response.getMessage()).contains("퇴장");
//    }
//
//    private Meeting createTestMeeting(Long id) throws Exception {
//        Meeting meeting = Meeting.builder()
//                .meetingName("Test Meeting")
//                .information("Meeting Information")
//                .maxPeople(10)
//                .curPeople(1)
//                .status(MeetingStatus.IN_PROGRESS)
//                .build();
//        setFieldWithReflection(meeting, "id", id);
//        return meeting;
//    }
//
//    private void setFieldWithReflection(Object object, String fieldName, Object value) throws Exception {
//        Field field = object.getClass().getDeclaredField(fieldName);
//        field.setAccessible(true);
//        field.set(object, value);
//    }
//}
