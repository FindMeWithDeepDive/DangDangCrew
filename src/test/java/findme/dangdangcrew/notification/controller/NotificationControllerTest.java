package findme.dangdangcrew.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import findme.dangdangcrew.chat.repository.ChatMessageRepository;
import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import findme.dangdangcrew.notification.domain.NotificationType;
import findme.dangdangcrew.notification.repository.NotificationRepository;
import findme.dangdangcrew.place.service.PlaceService;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Spring Boot의 테스트 환경에서 MockMvc를 자동으로 설정해주는 어노테이션입니다.
@Transactional
@ActiveProfiles("test")
class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @MockitoBean
    private ChatMessageRepository chatMessageRepository;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private PlaceService placeService;

    private User testUser;
    @BeforeEach
    void init(){
        testUser = User.builder()
                .email("test@gmail.com")
                .name("test")
                .nickname("test")
                .phoneNumber("01011112222")
                .password("test1234")
                .build();
        userRepository.save(testUser);
    }

    private void setAuthenticationTestUser(){
        String email = "test@gmail.com";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.emptyList()
                )
        );
    }

    @Test
    @DisplayName("유저의 알림들을 조회합니다.")
    void getUserNotificationsTest() throws Exception {
        // given
        setAuthenticationTestUser();
        String page = "1";
        LocalDateTime now = LocalDateTime.now();
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .applyUserId(1L)
                .placeId("12345")
                .targetUserId(2L)
                .message("test message")
                .meetingId(1L)
                .meetingLeaderId(1L)
                .isRead(false)
                .notificationType(NotificationType.LEADER_ACTION)
                .createdAt(now)
                .build();
        notificationRepository.save(notificationEntity);

        // when
        // then
        mockMvc.perform(get("/api/v1/notifications")
                .param("page", page)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.msg").value("유저가 즐겨찾기한 장소를 성공적으로 조회하였습니다."),
                        jsonPath("$.data.pages").value(1),
                        jsonPath("$.data.total").value(1),
                        jsonPath("$.data.results.applyUserId").value(1L),
                        jsonPath("$.data.results.placeId").value("12345"),
                        jsonPath("$.data.results.targetUserId").value(2L),
                        jsonPath("$.data.results.message").value("test message"),
                        jsonPath("$.data.results.meetingId").value(1L),
                        jsonPath("$.data.results.meetingLeaderId").value(1L),
                        jsonPath("$.data.results.isRead").value(false),
                        jsonPath("$.data.results.notificationType").value(NotificationType.LEADER_ACTION),
                        jsonPath("$.data.results.createdAt").value(now)
                        ).andDo(print());
    }

    @Test
    void readNotification() {
    }
}