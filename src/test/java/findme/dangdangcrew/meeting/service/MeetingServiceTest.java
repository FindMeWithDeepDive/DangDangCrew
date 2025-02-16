package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.chat.service.ChatRoomService;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.meeting.dto.MeetingDetailResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingRequestDto;
import findme.dangdangcrew.meeting.dto.MeetingUserResponseDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.mapper.MeetingMapper;
import findme.dangdangcrew.meeting.repository.MeetingRepository;
import findme.dangdangcrew.notification.event.NewMeetingEvent;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.service.PlaceService;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetingServiceTest {

    @InjectMocks
    private MeetingService meetingService;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MeetingMapper meetingMapper;

    @Mock
    private UserMeetingService userMeetingService;

    @Mock
    private UserService userService;

    @Mock
    private PlaceService placeService;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private EventPublisher eventPublisher;

    private Meeting meeting;
    private User user;
    private Place place;
    private UserMeeting userMeeting;
    private MeetingRequestDto meetingRequestDto;
    private MeetingDetailResponseDto meetingDetailResponseDto;
    private MeetingUserResponseDto meetingUserResponseDto;
    private PlaceRequestDto placeRequestDto;

    @BeforeEach
    void setup() {
        user = createUser();
        placeRequestDto = createPlaceRequestDto();
        place = createPlace();
        meetingRequestDto = createMeetingRequestDto();
        meeting = createMeeting();
        userMeeting = createUserMeeting();
        meetingDetailResponseDto = createMeetingDetailResponseDto();
        meetingUserResponseDto = createMeetingUserResponseDto(userMeeting);
    }

    private User createUser(){
        return User.builder()
                .email("bugak@email.com")
                .name("이부각")
                .nickname("뿌야")
                .password("bugak123")
                .phoneNumber("01012341234")
                .build();
    }

    private Place createPlace(){
        return Place.builder()
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
    }

    private PlaceRequestDto createPlaceRequestDto(){
        return new PlaceRequestDto("217787831", "원앤온리", "126.319192490757", "33.2392223486155");
    }

    private Meeting createMeeting(){
        return Meeting.builder()
                .id(1L)
                .meetingName("테스트용")
                .information("테스트용 모임입니다.")
                .maxPeople(5)
                .curPeople(2)
                .status(MeetingStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .place(place)
                .build();
    }

    private UserMeeting createUserMeeting(){
        return UserMeeting.builder()
                .id(1L)
                .user(user)
                .meeting(meeting)
                .status(UserMeetingStatus.LEADER)
                .build();
    }

    private MeetingRequestDto createMeetingRequestDto(){
        return MeetingRequestDto.builder()
                .meetingName("테스트용")
                .information("테스트용 모임입니다.")
                .maxPeople(5)
                .placeRequestDto(placeRequestDto)
                .build();
    }

    private MeetingDetailResponseDto createMeetingDetailResponseDto(){
        return MeetingDetailResponseDto.builder()
                .meetingId(1L)
                .meetingName("테스트용")
                .information("테스트용 모임입니다.")
                .maxPeople(5)
                .curPeople(2)
                .status(MeetingStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private MeetingUserResponseDto createMeetingUserResponseDto(UserMeeting userMeeting) {
        return MeetingUserResponseDto.builder()
                .userId(userMeeting.getUser().getId())
                .userName(userMeeting.getUser().getNickname())
                .meetingId(userMeeting.getMeeting().getId())
                .userMeetingStatus(userMeeting.getStatus())
                .build();
    }

    @Test
    @DisplayName("[✅모임 생성] 모임을 성공적으로 생성합니다.")
    void createMeeting_Success(){
        when(userService.getCurrentUser()).thenReturn(user);
        when(placeService.findOrCreatePlace(any())).thenReturn(place);
        when(meetingMapper.toEntity(any(), any())).thenReturn(meeting);
        when(meetingRepository.save(any())).thenReturn(meeting);
        when(userMeetingService.createUserMeeting(any(), any(), any())).thenReturn(userMeeting);
        when(meetingMapper.toUserDto(any())).thenReturn(meetingUserResponseDto);

        doNothing().when(chatRoomService).createChatRoom(any(Meeting.class));
        doNothing().when(eventPublisher).publisher(any(NewMeetingEvent.class));

        MeetingUserResponseDto response = meetingService.create(meetingRequestDto);

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(meeting.getId(), response.getMeetingId());
    }

    @Test
    @DisplayName("[❌ 모임 생성] 최대 인원 초과로 인해 모임 생성에 실패합니다.")
    void createMeeting_InvalidMaxPeople() {
        meetingRequestDto = MeetingRequestDto.builder()
                .meetingName("잘못된 모임")
                .information("테스트")
                .maxPeople(15)
                .placeRequestDto(placeRequestDto)
                .build();

        CustomException exception = assertThrows(CustomException.class,
                () -> meetingService.create(meetingRequestDto));

        assertEquals(ErrorCode.INVALID_MEETING_CAPACITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("[✅ 모임 조회] 모임 상세 정보를 정상적으로 조회합니다.")
    void readByMeetingId_Success() {
        when(meetingRepository.findByIdAndStatusIn(eq(meeting.getId()), any())).thenReturn(Optional.of(meeting));
        when(meetingMapper.toDto(any())).thenReturn(meetingDetailResponseDto);

        MeetingDetailResponseDto response = meetingService.readByMeetingId(meeting.getId());

        assertNotNull(response);
    }

    @Test
    @DisplayName("[❌ 모임 조회 실패] 존재하지 않는 모임 조회 시 예외를 발생시킵니다.")
    void readByMeetingId_NotFound() {
        Long nonExistingMeetingId = 999L;
        when(meetingRepository.findByIdAndStatusIn(eq(nonExistingMeetingId), any()))
                .thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> meetingService.readByMeetingId(nonExistingMeetingId));

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }
}
