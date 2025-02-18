package findme.dangdangcrew.meeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import findme.dangdangcrew.chat.repository.ChatRoomRepository;
import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.meeting.dto.MeetingRequestDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.repository.MeetingRepository;
import findme.dangdangcrew.meeting.repository.UserMeetingRepository;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.repository.PlaceRepository;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MeetingControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserMeetingRepository userMeetingRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private Meeting testMeeting;
    private Place testPlace;
    private User testLeader;
    private User testUser;
    private UserMeeting testUserMeeting;

    @BeforeEach
    void setUp() {
        testLeader = User.builder()
                .email("bugak@email.com")
                .name("이부각")
                .nickname("뿌야")
                .password("bugak123")
                .phoneNumber("01012341234")
                .build();

        userRepository.saveAndFlush(testLeader);

        testUser = User.builder()
                .email("0sook@email.com")
                .name("이영숙")
                .nickname("앵이")
                .password("aeng123")
                .phoneNumber("01043214321")
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

        testMeeting = meetingRepository.saveAndFlush(Meeting.builder()
                .meetingName("테스트 모임")
                .information("통합 테스트용 모임")
                .maxPeople(10)
                .curPeople(1)
                .status(MeetingStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .place(testPlace)
                .build());

        testUserMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testLeader)
                .status(UserMeetingStatus.LEADER)
                .build());
    }

    @AfterEach
    @Transactional
    void tearDown() {
        chatRoomRepository.deleteAllInBatch();
        userMeetingRepository.deleteAllInBatch();
        meetingRepository.deleteAllInBatch();
        placeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    private String generateTestToken(User user) {
        return jwtTokenProvider.generateAccessToken(user.getEmail());
    }

    @Test
    @DisplayName("[✅모임 생성] 모임 생성을 성공적으로 처리합니다.")
    void createMeeting_Success() throws Exception {
        String token = generateTestToken(testLeader);

        PlaceRequestDto placeRequestDto = new PlaceRequestDto("217787831", "원앤온리", "126.319192490757", "33.2392223486155");
        MeetingRequestDto requestDto = new MeetingRequestDto();
        ReflectionTestUtils.setField(requestDto, "meetingName", "새로운 모임");
        ReflectionTestUtils.setField(requestDto, "information", "새로운 모임 설명");
        ReflectionTestUtils.setField(requestDto, "maxPeople", 10);
        ReflectionTestUtils.setField(requestDto, "placeRequestDto", placeRequestDto);

        mockMvc.perform(post("/api/v1/meetings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임을 성공적으로 생성하였습니다."));
    }

    @Test
    @DisplayName("[✅모임 상세 조회] 모임 상세 조회를 성공적으로 처리합니다.")
    void getMeetingDetail_Success() throws Exception {
        String token = generateTestToken(testUser);

        mockMvc.perform(get("/api/v1/meetings/{meetingId}", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임 정보 조회를 성공하였습니다."))
                .andExpect(jsonPath("$.data.meetingName").value(testMeeting.getMeetingName()));
    }

    @Test
    @DisplayName("[❌모임 상세 조회] 해당 모임을 찾을 수 없는 경우 예외를 발생 시킵니다.")
    void getMeetingDetail_MeetingNotFound_Fail() throws Exception {
        String token = generateTestToken(testUser);

        mockMvc.perform(get("/api/v1/meetings/{meetingId}", 1231232)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 미팅을 찾을 수  없습니다."));
    }

    @Test
    @DisplayName("[✅장소별 모임 조회] 장소별 모임 조회를 성공적으로 처리합니다.")
    void getAllMeetingsByPlaceId_Success() throws Exception {
        String token = generateTestToken(testUser);

        mockMvc.perform(get("/api/v1/meetings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("placeId", testPlace.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("아이디가 " + testPlace.getId() + "인 장소에 생성된 모든 모임 조회에 성공하였습니다."))
                .andExpect(jsonPath("$.data[0].meetingName").value(testMeeting.getMeetingName()));
    }

    @Test
    @DisplayName("[✅모임 참가 신청] 모임 참가 신청을 성공적으로 처리합니다.")
    void applyMeeting_Success() throws Exception {
        String token = generateTestToken(testUser);

        mockMvc.perform(post("/api/v1/meetings/{meetingId}/applications", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임 참가 신청을 성공적으로 완료하였습니다."))
                .andExpect(jsonPath("$.data.meetingId").value(testMeeting.getId()));
    }

    @Test
    @DisplayName("[❌모임 참가 신청 실패] 이미 신청 내역이 존재하여 모임 참가 신청시 예외를 발생시킵니다.")
    void applyMeeting_ApplicationsAlreadyExists_Fail() throws Exception {
        String token = generateTestToken(testUser);

        testUserMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testUser)
                .status(UserMeetingStatus.CANCELLED)
                .build());

        mockMvc.perform(post("/api/v1/meetings/{meetingId}/applications", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("신청 내역이 존재합니다."));
    }

    @Test
    @DisplayName("[✅모임 참가 취소 성공] 모임 참가 취소를 성공적으로 처리합니다.")
    void cancelMeeting_Success() throws Exception {
        String token = generateTestToken(testUser);

        testUserMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testUser)
                .status(UserMeetingStatus.WAITING)
                .build());

        mockMvc.perform(patch("/api/v1/meetings/{meetingId}/applications", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임 참가를 성공적으로 취소하였습니다."))
                .andExpect(jsonPath("$.data.meetingId").value(testMeeting.getId()));
    }

    @Test
    @DisplayName("[❌모임 참가 취소 실패] 리더는 모임 참가 취소를 할 수 없어 예외를 발생 시킵니다.")
    void cancelMeeting_LeaderCannotCancel_Fail() throws Exception {
        String token = generateTestToken(testLeader);

        mockMvc.perform(patch("/api/v1/meetings/{meetingId}/applications", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("리더는 참가 취소를 할 수 없습니다."));
    }

    @Test
    @DisplayName("[❌모임 참가 취소 실패] 해당 모임을 찾을 수 없어 예외를 발생시킵니다.")
    void cancelMeeting_MeetingNotFound_Fail() throws Exception {
        String token = generateTestToken(testLeader);

        mockMvc.perform(patch("/api/v1/meetings/{meetingId}/applications", 1231232)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 미팅을 찾을 수  없습니다."));
    }

    @Test
    @DisplayName("[✅모임 확정자 조회] 모임 확정자 조회를 성공적으로 처리합니다.")
    void getConfirmedMeetingApplications_Success() throws Exception {
        String token = generateTestToken(testUser);

        testUserMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testUser)
                .status(UserMeetingStatus.CONFIRMED)
                .build());

        mockMvc.perform(get("/api/v1/meetings/{meetingId}/confirmed", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임 확정자를 성공적으로 조회하였습니다."))
                .andExpect(jsonPath("$.data[0].userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("[✅내 모임 조회] 내 모임 조회를 성공적으로 처리합니다.")
    void getMeetingsByUser_Success() throws Exception {
        String token = generateTestToken(testUser);

        testUserMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testUser)
                .status(UserMeetingStatus.CONFIRMED)
                .build());

        mockMvc.perform(get("/api/v1/meetings/my")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("가입한 모든 모임을 성공적으로 조회하였습니다."))
                .andExpect(jsonPath("$.data[0].meetingId").value(testMeeting.getId()));
    }
}
