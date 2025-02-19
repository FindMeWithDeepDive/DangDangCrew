package findme.dangdangcrew.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import findme.dangdangcrew.chat.repository.ChatMessageRepository;
import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import findme.dangdangcrew.notification.domain.NotificationType;
import findme.dangdangcrew.notification.repository.NotificationRepository;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.repository.PlaceRepository;
import findme.dangdangcrew.place.service.PlaceService;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // Spring Boot의 테스트 환경에서 MockMvc를 자동으로 설정해주는 어노테이션입니다.
@ActiveProfiles("test")
@Transactional
class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PlaceRepository placeRepository;

    private User testLeaderUser;
    private User testUser;
    private Place testPlace;

    @BeforeEach
    void init(){
        testLeaderUser = User.builder()
                .email("leader@gmail.com")
                .name("leader")
                .nickname("leader")
                .phoneNumber("01011112222")
                .password("test1234")
                .build();
        userRepository.saveAndFlush(testLeaderUser);
        testUser = User.builder()
                .email("user@gmail.com")
                .name("user")
                .nickname("user")
                .phoneNumber("01011113333")
                .password("test1234")
                .build();
        userRepository.saveAndFlush(testUser);

        testPlace = Place.builder()
                .id("217787831")
                .placeName("원앤온리")
                .x(126.319192490757)
                .y(33.2392223486155)
                .categoryGroupCode("CE7")
                .categoryGroupName("카페")
                .phone("064-794-0117")
                .placeUrl("http://place.map.kakao.com/217787831")
                .addressName("제주특별자치도 서귀포시 안덕면 사계리 86")
                .roadAddressName("제주특별자치도 서귀포시 안덕면 산방로 141")
                .build();
        testPlace = placeRepository.saveAndFlush(testPlace);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        placeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    private String generateTestToken(User user) {
        return jwtTokenProvider.generateAccessToken(user.getEmail());
    }

    @Test
    @DisplayName("유저가 알림을 조회합니다.")
    void getUserNotificationsTest() throws Exception {
        // given
        String testToken = generateTestToken(testUser);
        String page = "1";
        LocalDateTime now = LocalDateTime.now();
        Long targetUserId = testUser.getId();
        Long meetingId = 1L;

        NotificationEntity notificationEntity = NotificationEntity.builder()
                .targetUserId(targetUserId)
                .message("test message")
                .meetingId(meetingId)
                .isRead(false)
                .notificationType(NotificationType.LEADER_ACTION)
                .createdAt(now)
                .build();
        notificationRepository.saveAndFlush(notificationEntity);

        // when
        // then
        mockMvc.perform(get("/api/v1/notifications")
                .header("Authorization", "Bearer " + testToken)
                .param("page", page)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.msg").value("유저의 알림을 성공적으로 조회하였습니다."),
                        jsonPath("$.data.pages").value(1),
                        jsonPath("$.data.total").value(1),
                        jsonPath("$.data.results[0].id").value(notificationEntity.getId()),
                        jsonPath("$.data.results[0].targetUserId").value(targetUserId),
                        jsonPath("$.data.results[0].message").value("test message"),
                        jsonPath("$.data.results[0].meetingId").value(meetingId),
                        jsonPath("$.data.results[0].read").value(false),
                        jsonPath("$.data.results[0].notificationType").value("LEADER_ACTION"))
                .andDo(print());
    }

    @Test
    @DisplayName("유저가 알림을 읽음 처리 합니다.")
    void readNotification() throws Exception {
        // given
        String testToken = generateTestToken(testUser);
        LocalDateTime now = LocalDateTime.now();
        Long targetUserId = testUser.getId();
        Long meetingId = 1L;

        NotificationEntity notificationEntity = NotificationEntity.builder()
                .targetUserId(targetUserId)
                .message("test message")
                .meetingId(meetingId)
                .isRead(false)
                .notificationType(NotificationType.LEADER_ACTION)
                .createdAt(now)
                .build();
        notificationRepository.saveAndFlush(notificationEntity);
        Long notificationId = notificationEntity.getId();

        // when
        // then
        mockMvc.perform(patch("/api/v1/notifications/"+notificationId+"/read")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.msg").value(notificationId + "번 알림이 읽음 처리되었습니다."))
                .andDo(print());
    }
}