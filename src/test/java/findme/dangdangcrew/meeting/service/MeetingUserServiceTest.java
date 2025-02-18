package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.global.service.RedisService;
import findme.dangdangcrew.meeting.MeetingTestData;
import findme.dangdangcrew.meeting.dto.MeetingUserResponseDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.mapper.MeetingMapper;
import findme.dangdangcrew.notification.event.ApplyEvent;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingUserServiceTest {

    @InjectMocks
    private MeetingUserService meetingUserService;

    @Mock
    private MeetingService meetingService;

    @Mock
    private UserService userService;

    @Mock
    private UserMeetingService userMeetingService;

    @Mock
    private MeetingMapper meetingMapper;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private RedisService redisService;

    private Meeting meeting;
    private User leader;
    private User user;
    private UserMeeting userMeeting;
    private MeetingUserResponseDto meetingUserResponseDto;
    private Place place;

    @BeforeEach
    void setup() {
        leader = MeetingTestData.createUser();
        user = MeetingTestData.createUser1();
        place = MeetingTestData.createPlace();
        meeting = MeetingTestData.createMeeting(MeetingStatus.IN_PROGRESS);
        userMeeting = MeetingTestData.createUserMeeting(user, meeting, UserMeetingStatus.WAITING);
        meetingUserResponseDto = MeetingTestData.createMeetingUserResponseDto(userMeeting);
    }

    @Test
    @DisplayName("[✅ 모임 참가 신청] - 성공적으로 참가 신청을 처리합니다.")
    void applyMeeting_Success() {
        when(meetingService.findProgressMeeting(anyLong())).thenReturn(meeting);
        when(userService.getCurrentUser()).thenReturn(user);
        doNothing().when(userMeetingService).isUserAlreadyInMeeting(any(), any());
        when(userMeetingService.createUserMeeting(any(), any(), any())).thenReturn(userMeeting);
        when(userMeetingService.findLeader(meeting)).thenReturn(leader);
        when(meetingMapper.toUserDto(any())).thenReturn(meetingUserResponseDto);

        doNothing().when(eventPublisher).publisher(any(ApplyEvent.class));
        doNothing().when(redisService).incrementHotPlace(place.getId());

        MeetingUserResponseDto response = meetingUserService.applyMeetingByMeetingId(meeting.getId());

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(meeting.getId(), response.getMeetingId());

        verify(meetingService).findProgressMeeting(meeting.getId());
        verify(userService).getCurrentUser();
        verify(userMeetingService).isUserAlreadyInMeeting(meeting, user);
        verify(userMeetingService).createUserMeeting(meeting, user, UserMeetingStatus.WAITING);
        verify(userMeetingService).findLeader(meeting);
    }

    @Test
    @DisplayName("[❌모임 참가 신청] - 모임을 찾을 수 없는 경우 예외를 발생 시킵니다.")
    void applyMeeting_CannotFindMeeting_Fail(){
        doThrow(new CustomException(ErrorCode.MEETING_NOT_FOUND))
                .when(meetingService).findProgressMeeting(999L);

        CustomException exception = assertThrows(CustomException.class, () -> meetingUserService.applyMeetingByMeetingId(999L));

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("[❌모임 참가 신청] - 유저가 모임에 이미 참가한 경우 예외를 발생시킵니다.")
    void applyMeeting_AlreadyJoined_Fail() {
        when(meetingService.findProgressMeeting(anyLong())).thenReturn(meeting);
        when(userService.getCurrentUser()).thenReturn(user);
        doThrow(new CustomException(ErrorCode.MEETING_ALREADY_EXISTS))
                .when(userMeetingService).isUserAlreadyInMeeting(any(), any());

        CustomException exception = assertThrows(CustomException.class, () -> meetingUserService.applyMeetingByMeetingId(meeting.getId()));

        assertEquals(ErrorCode.MEETING_ALREADY_EXISTS, exception.getErrorCode());

        verify(meetingService).findProgressMeeting(meeting.getId());
        verify(userService).getCurrentUser();
        verify(userMeetingService).isUserAlreadyInMeeting(meeting, user);
        verify(userMeetingService, never()).createUserMeeting(any(), any(), any());
    }

    @Test
    @DisplayName("[✅모임 참가 취소] - 유저가 성공적으로 참가 취소를 처리합니다.")
    void cancelMeeting_Success() {
        when(meetingService.findProgressMeeting(anyLong())).thenReturn(meeting);
        when(userService.getCurrentUser()).thenReturn(user);
        doNothing().when(userMeetingService).verifyUser(any(), any());
        when(userMeetingService.updateMeetingStatus(any(), any(), any())).thenReturn(userMeeting);
        when(meetingMapper.toUserDto(any())).thenReturn(meetingUserResponseDto);
        doNothing().when(meetingService).updateMeetingStatus(any());

        MeetingUserResponseDto response = meetingUserService.cancelMeetingApplication(meeting.getId());

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(meeting.getId(), response.getMeetingId());

        verify(meetingService).findProgressMeeting(meeting.getId());
        verify(userService).getCurrentUser();
        verify(userMeetingService).verifyUser(meeting, user);
        verify(userMeetingService).updateMeetingStatus(meeting, user, UserMeetingStatus.CANCELLED);
        verify(meetingService).updateMeetingStatus(meeting);
    }

    @Test
    @DisplayName("[❌모임 참가 신청] - 모임을 찾을 수 없는 경우 예외를 발생 시킵니다.")
    void cancelMeeting_CannotFindMeeting_Fail(){
        doThrow(new CustomException(ErrorCode.MEETING_NOT_FOUND))
                .when(meetingService).findProgressMeeting(999L);

        CustomException exception = assertThrows(CustomException.class, () -> meetingUserService.cancelMeetingApplication(999L));

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("[❌모임 참가 취소] - 유저가 참가하지 않은 모임을 참가 취소할 경우 예외를 발생시킵니다.")
    void cancelMeeting_NotJoined_Fail() {
        when(meetingService.findProgressMeeting(anyLong())).thenReturn(meeting);
        when(userService.getCurrentUser()).thenReturn(user);
        doThrow(new CustomException(ErrorCode.MEETING_USER_NOT_EXISTS)).when(userMeetingService).verifyUser(any(), any());

        CustomException exception = assertThrows(CustomException.class, () -> meetingUserService.cancelMeetingApplication(meeting.getId()));

        assertEquals(ErrorCode.MEETING_USER_NOT_EXISTS, exception.getErrorCode());

        verify(meetingService).findProgressMeeting(1L);
        verify(userService).getCurrentUser();
        verify(userMeetingService).verifyUser(meeting, user);
        verify(userMeetingService, never()).updateMeetingStatus(any(), any(), any());
    }
}
