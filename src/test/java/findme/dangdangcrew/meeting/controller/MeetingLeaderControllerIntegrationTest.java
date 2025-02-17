package findme.dangdangcrew.meeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import findme.dangdangcrew.chat.repository.ChatRoomRepository;
import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.meeting.dto.MeetingRequestDto;
import findme.dangdangcrew.meeting.dto.MeetingUserStatusUpdateRequestDto;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class MeetingLeaderControllerIntegrationTest {
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
    private UserMeeting testLeaderMeeting;
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

        testLeaderMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testLeader)
                .status(UserMeetingStatus.LEADER)
                .build());

        testUserMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testUser)
                .status(UserMeetingStatus.WAITING)
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
    @DisplayName("✅ 신청 유저 확정/취소 성공")
    void createMeeting_Success() throws Exception {
        String token = generateTestToken(testLeader);

        MeetingUserStatusUpdateRequestDto requestDto = new MeetingUserStatusUpdateRequestDto();
        ReflectionTestUtils.setField(requestDto, "userId", testUser.getId());
        ReflectionTestUtils.setField(requestDto, "status", UserMeetingStatus.CONFIRMED);

        mockMvc.perform(put("/api/v1/meetings/leader/{meetingId}/applications", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임 신청을 한 유저들의 상태를 성공적으로 변경하였습니다."))
                .andExpect(jsonPath("$.data.userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("✅ 모임 신청자 조회 - 성공")
    void getMeetingDetail_Success() throws Exception {
        String token = generateTestToken(testLeader);

        mockMvc.perform(get("/api/v1/meetings/leader/{meetingId}/applications", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임 참가자 목록을 성공적으로 조회하였습니다."))
                .andExpect(jsonPath("$.data[0].userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("✅ 신청 유저 확정/취소 성공")
    void updateMeeting_Success() throws Exception {
        String token = generateTestToken(testLeader);


        MeetingRequestDto requestDto = new MeetingRequestDto();
        ReflectionTestUtils.setField(requestDto, "meetingName", "수정");
        ReflectionTestUtils.setField(requestDto, "information", "수정합니덩");
        ReflectionTestUtils.setField(requestDto, "maxPeople", 6);
        ReflectionTestUtils.setField(requestDto, "placeRequestDto", new PlaceRequestDto("217787831", "원앤온리", "126.319192490757", "33.2392223486155"));

        mockMvc.perform(put("/api/v1/meetings/leader/{meetingId}", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임 수정을 성공적으로 완료하였습니다."))
                .andExpect(jsonPath("$.data.meetingId").value(testMeeting.getId()));
    }

    @Test
    @DisplayName("✅ 모임 삭제 - 성공")
    void deleteMeeting_Success() throws Exception {
        String token = generateTestToken(testLeader);

        mockMvc.perform(patch("/api/v1/meetings/leader/{meetingId}/cancel", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임 삭제를 성공적으로 완료하였습니다."));
    }

    @Test
    @DisplayName("✅ 모임 종료 - 성공")
    void quitMeeting_Success() throws Exception {
        String token = generateTestToken(testLeader);

        testUserMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testUser)
                .status(UserMeetingStatus.CONFIRMED)
                .build());

        mockMvc.perform(patch("/api/v1/meetings/leader/{meetingId}/quit", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("모임 종료를 성공적으로 완료하였습니다."))
                .andExpect(jsonPath("$.data[0].userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("✅ 모임 참석 여부 변경 - 성공")
    void checkUserMeetingStatus_Success() throws Exception {
        String token = generateTestToken(testLeader);

        testMeeting = meetingRepository.saveAndFlush(Meeting.builder()
                .meetingName("테스트 모임")
                .information("통합 테스트용 모임")
                .maxPeople(10)
                .curPeople(1)
                .status(MeetingStatus.CLOSED)
                .createdAt(LocalDateTime.now())
                .place(testPlace)
                .build());

        testLeaderMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testLeader)
                .status(UserMeetingStatus.LEADER)
                .build());

        testUserMeeting = userMeetingRepository.saveAndFlush(UserMeeting.builder()
                .meeting(testMeeting)
                .user(testUser)
                .status(UserMeetingStatus.CONFIRMED)
                .build());

        MeetingUserStatusUpdateRequestDto dto = new MeetingUserStatusUpdateRequestDto();
        ReflectionTestUtils.setField(dto, "userId", testUser.getId());
        ReflectionTestUtils.setField(dto, "status", UserMeetingStatus.ATTENDED);
        List<MeetingUserStatusUpdateRequestDto> requestDto = List.of(dto);

        mockMvc.perform(patch("/api/v1/meetings/leader/{meetingId}/attendance", testMeeting.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(requestDto.size() + "개의 유저들의 상태를 성공적으로 변경하였습니다."))
                .andExpect(jsonPath("$.data[0].userId").value(testUser.getId()));
    }
}
