package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.chat.service.ChatRoomService;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.meeting.MeetingTestData;
import findme.dangdangcrew.meeting.dto.MeetingDetailResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingRequestDto;
import findme.dangdangcrew.meeting.dto.MeetingUserResponseDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
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
        user = MeetingTestData.createUser();
        placeRequestDto = MeetingTestData.createPlaceRequestDto();
        place = MeetingTestData.createPlace();
        meetingRequestDto = MeetingTestData.createMeetingRequestDto(placeRequestDto);
        meeting = MeetingTestData.createMeeting(place);
        userMeeting = MeetingTestData.createUserMeeting(user, meeting);
        meetingDetailResponseDto = MeetingTestData.createMeetingDetailResponseDto();
        meetingUserResponseDto = MeetingTestData.createMeetingUserResponseDto(userMeeting);
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
    @DisplayName("[❌모임 생성] 최대 인원 초과로 인해 모임 생성에 실패합니다.")
    void createMeeting_InvalidMaxPeople_Fail() {
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
    @DisplayName("[✅모임 조회] 모임 상세 정보를 정상적으로 조회합니다.")
    void readByMeetingId_Success() {
        when(meetingRepository.findByIdAndStatusIn(eq(meeting.getId()), any())).thenReturn(Optional.of(meeting));
        when(meetingMapper.toDto(any())).thenReturn(meetingDetailResponseDto);

        MeetingDetailResponseDto response = meetingService.readByMeetingId(meeting.getId());

        assertNotNull(response);
    }

    @Test
    @DisplayName("[❌모임 조회] 존재하지 않는 모임 조회 시 예외를 발생시킵니다.")
    void readByMeetingId_NotFound_Fail() {
        Long nonExistingMeetingId = 999L;
        when(meetingRepository.findByIdAndStatusIn(eq(nonExistingMeetingId), any()))
                .thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> meetingService.readByMeetingId(nonExistingMeetingId));

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }
}
