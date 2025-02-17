package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.global.service.RedisService;
import findme.dangdangcrew.meeting.MeetingTestData;
import findme.dangdangcrew.meeting.dto.MeetingUserResponseDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.mapper.MeetingMapper;
import findme.dangdangcrew.meeting.repository.MeetingRepository;
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

import java.util.Optional;

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

    @Mock
    private MeetingRepository meetingRepository;

    private Meeting meeting;
    private User user;
    private UserMeeting userMeeting;
    private Place place;
    private MeetingUserResponseDto meetingUserResponseDto;

    @BeforeEach
    void setup() {
        user = MeetingTestData.createUser();
        place = MeetingTestData.createPlace();
        meeting = MeetingTestData.createMeeting(place);
        userMeeting = MeetingTestData.createUserMeeting(user, meeting);
        meetingUserResponseDto = MeetingTestData.createMeetingUserResponseDto(userMeeting);
    }

    @Test
    @DisplayName("[✅ 모임 참가 신청] 성공적으로 참가 신청을 처리합니다.")
    void applyMeeting_Success() {
        when(meetingService.findProgressMeeting(anyLong())).thenReturn(meeting);
        when(userService.getCurrentUser()).thenReturn(user);
        doNothing().when(userMeetingService).isUserAlreadyInMeeting(any(), any());
        when(userMeetingService.createUserMeeting(any(), any(), any())).thenReturn(userMeeting);
        when(userMeetingService.findLeader(any())).thenReturn(user);
        when(meetingMapper.toUserDto(any())).thenReturn(meetingUserResponseDto);
        doNothing().when(eventPublisher).publisher(any(ApplyEvent.class));
        doNothing().when(redisService).incrementHotPlace(anyString());

        MeetingUserResponseDto response = meetingUserService.applyMeetingByMeetingId(1L);

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(meeting.getId(), response.getMeetingId());

        verify(meetingService).findProgressMeeting(1L);
        verify(userService).getCurrentUser();
        verify(userMeetingService).isUserAlreadyInMeeting(meeting, user);
        verify(userMeetingService).createUserMeeting(meeting, user, UserMeetingStatus.WAITING);
        verify(eventPublisher).publisher(any(ApplyEvent.class));
        verify(redisService).incrementHotPlace(place.getId());
    }

    // 모임을 찾을 수 없는경우
    @Test
    @DisplayName("[❌모임 참가 신청] 모임을 찾을 수 없는 경우 예외를 발생 시킵니다.")
    void applyMeeting_CannotFindMeeting_Fail(){
        Long nonExistingMeetingId = 999L;
        doThrow(new CustomException(ErrorCode.MEETING_NOT_FOUND)).when(meetingService).findProgressMeeting(nonExistingMeetingId);

        CustomException exception = assertThrows(CustomException.class, () -> meetingUserService.applyMeetingByMeetingId(nonExistingMeetingId));

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("[❌모임 참가 신청] 이미 참가한 경우 예외를 발생시킵니다.")
    void applyMeeting_AlreadyJoined_Fail() {
        // given
        when(meetingService.findProgressMeeting(anyLong())).thenReturn(meeting);
        when(userService.getCurrentUser()).thenReturn(user);
        doThrow(new CustomException(ErrorCode.MEETING_ALREADY_EXISTS)).when(userMeetingService).isUserAlreadyInMeeting(any(), any());

        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingUserService.applyMeetingByMeetingId(1L);
        });

        assertEquals(ErrorCode.MEETING_ALREADY_EXISTS, exception.getErrorCode());

        verify(meetingService).findProgressMeeting(1L);
        verify(userService).getCurrentUser();
        verify(userMeetingService).isUserAlreadyInMeeting(meeting, user);
        verify(userMeetingService, never()).createUserMeeting(any(), any(), any());
    }

    @Test
    @DisplayName("[✅모임 참가 취소] 성공적으로 참가 취소를 처리합니다.")
    void cancelMeeting_Success() {
        when(meetingService.findProgressMeeting(anyLong())).thenReturn(meeting);
        when(userService.getCurrentUser()).thenReturn(user);
        doNothing().when(userMeetingService).verifyUser(any(), any());
        when(userMeetingService.updateMeetingStatus(any(), any(), any())).thenReturn(userMeeting);
        when(meetingMapper.toUserDto(any())).thenReturn(meetingUserResponseDto);
        doNothing().when(meetingService).updateMeetingStatus(any());

        MeetingUserResponseDto response = meetingUserService.cancelMeetingApplication(1L);

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(meeting.getId(), response.getMeetingId());

        verify(meetingService).findProgressMeeting(1L);
        verify(userService).getCurrentUser();
        verify(userMeetingService).verifyUser(meeting, user);
        verify(userMeetingService).updateMeetingStatus(meeting, user, UserMeetingStatus.CANCELLED);
        verify(meetingService).updateMeetingStatus(meeting);
    }

    @Test
    @DisplayName("[❌모임 참가 취소] 참가하지 않은 경우 예외를 발생시킵니다.")
    void cancelMeeting_NotJoined_Fail() {
        when(meetingService.findProgressMeeting(anyLong())).thenReturn(meeting);
        when(userService.getCurrentUser()).thenReturn(user);
        doThrow(new CustomException(ErrorCode.MEETING_USER_NOT_EXISTS)).when(userMeetingService).verifyUser(any(), any());

        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingUserService.cancelMeetingApplication(1L);
        });

        assertEquals(ErrorCode.MEETING_USER_NOT_EXISTS, exception.getErrorCode());

        verify(meetingService).findProgressMeeting(1L);
        verify(userService).getCurrentUser();
        verify(userMeetingService).verifyUser(meeting, user);
        verify(userMeetingService, never()).updateMeetingStatus(any(), any(), any());
    }

    @Test
    @DisplayName("[❌모임 참가 취소] 리더가 모임 참가 취소를 신청하면 예외를 발생 시킵니다.")
    void cancelMeeting_LeaderCannotCancel_Fail() {
        when(meetingService.findProgressMeeting(anyLong())).thenReturn(meeting);
        when(userService.getCurrentUser()).thenReturn(user);
        doThrow(new CustomException(ErrorCode.LEADER_CANNOT_CANCEL)).when(userMeetingService).verifyUser(any(), any());

        CustomException exception = assertThrows(CustomException.class, () -> {
            meetingUserService.cancelMeetingApplication(meeting.getId());
        });

        assertEquals(ErrorCode.LEADER_CANNOT_CANCEL, exception.getErrorCode());
    }
}
