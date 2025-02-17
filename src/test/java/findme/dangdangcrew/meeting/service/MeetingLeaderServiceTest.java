package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.mapper.MeetingMapper;
import findme.dangdangcrew.meeting.repository.MeetingRepository;
import findme.dangdangcrew.notification.event.LeaderActionEvent;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static findme.dangdangcrew.meeting.MeetingTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetingLeaderServiceTest {

    @InjectMocks
    private MeetingLeaderService meetingLeaderService;

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
    private MeetingRepository meetingRepository;

    private Meeting meeting;
    private Meeting closedMeeting;
    private User applicant;
    private UserMeeting leaderMeeting;
    private UserMeeting userMeeting;
    private MeetingUserResponseDto meetingUserResponseDto;
    private MeetingUserStatusUpdateRequestDto meetingUserStatusUpdateRequestDto;

    @BeforeEach
    void setup() {
        User leader = createUser();
        applicant = createUser1();
        meeting = createMeeting(MeetingStatus.IN_PROGRESS);
        closedMeeting = createMeeting(MeetingStatus.CLOSED);
        leaderMeeting = createUserMeeting(leader, meeting, UserMeetingStatus.LEADER);
        userMeeting = createUserMeeting(applicant, meeting, UserMeetingStatus.CONFIRMED);
        meetingUserResponseDto = createMeetingUserResponseDto(leaderMeeting);
        meetingUserStatusUpdateRequestDto = createMeetingUserStatusUpdateRequestDto(applicant, UserMeetingStatus.ATTENDED);
    }

    @Test
    @DisplayName("[✅모임 신청 상태 변경] - 리더가 신청자의 상태를 성공적으로 변경합니다.")
    void changeMeetingApplicationStatusByLeader_Accept() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userService.getUser(applicant.getId())).thenReturn(applicant);

        meetingUserStatusUpdateRequestDto = createMeetingUserStatusUpdateRequestDto(applicant, UserMeetingStatus.CONFIRMED);
        leaderMeeting = createUserMeeting(applicant, meeting, UserMeetingStatus.WAITING);

        when(userMeetingService.getUserMeeting(meeting, applicant)).thenReturn(leaderMeeting);
        when(userMeetingService.updateMeetingStatus(meeting, applicant, UserMeetingStatus.CONFIRMED)).thenReturn(leaderMeeting);
        when(meetingMapper.toUserDto(leaderMeeting)).thenReturn(meetingUserResponseDto);
        MeetingUserResponseDto response = meetingLeaderService.changeMeetingApplicationStatusByLeader(meeting.getId(), meetingUserStatusUpdateRequestDto);

        assertNotNull(response);
        verify(meetingService, times(1)).findProgressMeeting(meeting.getId());
        verify(userMeetingService, times(1)).updateMeetingStatus(meeting, applicant, UserMeetingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("[✅모임 신청 승인] - 모임 신청 승인 시 현재 인원이 증가해야 합니다.")
    void changeMeetingApplicationStatusByLeader_IncreasePeople() {
        UserMeeting waitingUserMeeting = createUserMeeting(applicant, meeting, UserMeetingStatus.WAITING);

        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userService.getUser(applicant.getId())).thenReturn(applicant);
        when(userMeetingService.getUserMeeting(meeting, applicant)).thenReturn(waitingUserMeeting);
        when(userMeetingService.updateMeetingStatus(meeting, applicant, UserMeetingStatus.CONFIRMED)).thenReturn(waitingUserMeeting);
        when(meetingMapper.toUserDto(waitingUserMeeting)).thenReturn(meetingUserResponseDto);
        doNothing().when(eventPublisher).publisher(any(LeaderActionEvent.class));

        int beforePeople = meeting.getCurPeople();

        MeetingUserResponseDto result = meetingLeaderService.changeMeetingApplicationStatusByLeader(meeting.getId(), createMeetingUserStatusUpdateRequestDto(applicant, UserMeetingStatus.CONFIRMED));

        assertNotNull(result);
        assertEquals(beforePeople + 1, meeting.getCurPeople());
        verify(meetingService, times(1)).updateMeetingStatus(meeting);
    }

    @Test
    @DisplayName("[✅모임 신청 강퇴] - 참가자 강퇴 시 현재 인원이 감소해야 합니다.")
    void changeMeetingApplicationStatusByLeader_DecreasePeople() {
        UserMeeting confirmedUserMeeting = createUserMeeting(applicant, meeting, UserMeetingStatus.CONFIRMED);

        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userService.getUser(applicant.getId())).thenReturn(applicant);
        when(userMeetingService.getUserMeeting(meeting, applicant)).thenReturn(confirmedUserMeeting);
        when(userMeetingService.updateMeetingStatus(meeting, applicant, UserMeetingStatus.CANCELLED)).thenReturn(confirmedUserMeeting);
        when(meetingMapper.toUserDto(confirmedUserMeeting)).thenReturn(meetingUserResponseDto);
        doNothing().when(eventPublisher).publisher(any(LeaderActionEvent.class));

        int beforePeople = meeting.getCurPeople();

        MeetingUserResponseDto result = meetingLeaderService.changeMeetingApplicationStatusByLeader(meeting.getId(), createMeetingUserStatusUpdateRequestDto(applicant, UserMeetingStatus.CANCELLED));

        assertNotNull(result);
        assertEquals(beforePeople - 1, meeting.getCurPeople());
        verify(meetingService, times(1)).updateMeetingStatus(meeting);
    }

    @Test
    @DisplayName("[✅ 모임 신청자 조회] - 리더가 모임 신청자들 목록을 성공적으로 조회합니다.")
    void readAllApplications() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userMeetingService.findWaitingByMeetingId(meeting)).thenReturn(List.of(leaderMeeting));
        when(meetingMapper.toListUserDto(List.of(leaderMeeting))).thenReturn(List.of(meetingUserResponseDto));

        List<MeetingUserResponseDto> result = meetingLeaderService.readAllApplications(meeting.getId());

        assertEquals(1, result.size());
        verify(meetingService, times(1)).findProgressMeeting(meeting.getId());
    }

    @Test
    @DisplayName("[✅모임 종료] - 리더가 모임을 종료하면 상태가 'CLOSED'로 변경됩니다.")
    void quitMeeting_Success() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userMeetingService.findConfirmedByMeetingId(meeting)).thenReturn(List.of(leaderMeeting));
        when(meetingMapper.toListMeetingUsersDto(List.of(leaderMeeting))).thenReturn(List.of(meetingUserResponseDto));

        List<MeetingUserResponseDto> result = meetingLeaderService.quitMeeting(meeting.getId());

        assertEquals(MeetingStatus.CLOSED, meeting.getStatus());
        assertEquals(1, result.size());
        verify(meetingService).findProgressMeeting(meeting.getId());
        verify(userMeetingService).verifyLeaderPermission(meeting);
    }

    @Test
    @DisplayName("[❌모임 종료] - 리더가 아닌 유저가 종료 시 예외가 발생합니다.")
    void quitMeeting_NoPermission_Fail() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doThrow(new CustomException(ErrorCode.NOT_LEADER)).when(userMeetingService).verifyLeaderPermission(meeting);

        CustomException exception = assertThrows(CustomException.class, () ->
                meetingLeaderService.quitMeeting(meeting.getId())
        );

        assertEquals(ErrorCode.NOT_LEADER, exception.getErrorCode());
    }

    @Test
    @DisplayName("[❌모임 종료] - 종료하고자 하는 모임을 찾을 수 없을 경우 예외가 발생합니다.")
    void quitMeeting_NotFoundMeeting_Fail() {
        Long nonExistingMeetingId = 999L;
        doThrow(new CustomException(ErrorCode.MEETING_NOT_FOUND)).when(meetingService).findProgressMeeting(nonExistingMeetingId);

        CustomException exception = assertThrows(CustomException.class, () ->
                meetingLeaderService.quitMeeting(nonExistingMeetingId)
        );

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("[✅참석 여부 변경] - 리더가 참가자의 상태를 정상적으로 변경합니다.")
    void checkAttendedOrAbsent_Success() {
        List<MeetingUserStatusUpdateRequestDto> updateDtos = List.of(createMeetingUserStatusUpdateRequestDto(applicant, UserMeetingStatus.ATTENDED));

        when(meetingRepository.findByIdAndStatus(closedMeeting.getId(), MeetingStatus.CLOSED)).thenReturn(Optional.ofNullable(closedMeeting));
        doNothing().when(userMeetingService).verifyLeaderPermission(closedMeeting);
        when(userMeetingService.findUserMeetingsByMeetingAndUserIds(closedMeeting, List.of(applicant.getId())))
                .thenReturn(List.of(userMeeting));
        when(meetingMapper.toListMeetingUsersDto(List.of(userMeeting))).thenReturn(List.of(meetingUserResponseDto));

        List<MeetingUserResponseDto> result = meetingLeaderService.checkAttendedOrAbsent(closedMeeting.getId(), updateDtos);

        assertNotNull(result);
        verify(userMeetingService).verifyLeaderPermission(closedMeeting);
        verify(userMeetingService).findUserMeetingsByMeetingAndUserIds(closedMeeting, List.of(applicant.getId()));
    }

    @Test
    @DisplayName("[❌참석 여부 변경] - 리더가 아닌 유저가 변경 시 예외 발생")
    void checkAttendedOrAbsent_NoPermission_Fail() {
        when(meetingRepository.findByIdAndStatus(closedMeeting.getId(), MeetingStatus.CLOSED))
                .thenReturn(Optional.ofNullable(closedMeeting));

        List<MeetingUserStatusUpdateRequestDto> updateDtos =
                List.of(createMeetingUserStatusUpdateRequestDto(applicant, UserMeetingStatus.ATTENDED));

        doThrow(new CustomException(ErrorCode.NOT_LEADER))
                .when(userMeetingService).verifyLeaderPermission(closedMeeting);

        CustomException exception = assertThrows(CustomException.class, () ->
                meetingLeaderService.checkAttendedOrAbsent(closedMeeting.getId(), updateDtos));

        assertEquals(ErrorCode.NOT_LEADER, exception.getErrorCode());
    }

    @Test
    @DisplayName("[✅모임 삭제] - 리더가 모임을 정상적으로 삭제합니다.")
    void deleteMeeting_Success() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        doNothing().when(userMeetingService).delete(meeting);

        meetingLeaderService.deleteMeeting(meeting.getId());

        assertEquals(MeetingStatus.DELETED, meeting.getStatus());
        verify(userMeetingService, times(1)).delete(meeting);
    }

    @Test
    @DisplayName("[❌모임 삭제] - 삭제할 모임을 찾을 수 없을 경우 예외 발생")
    void deleteMeeting_NotFound_Fail() {
        Long nonExistingMeetingId = 999L;
        doThrow(new CustomException(ErrorCode.MEETING_NOT_FOUND))
                .when(meetingService).findProgressMeeting(nonExistingMeetingId);

        CustomException exception = assertThrows(CustomException.class, () ->
                meetingLeaderService.deleteMeeting(nonExistingMeetingId));

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("[❌모임 삭제] - 리더가 아닌 유저가 삭제 시 예외 발생")
    void deleteMeeting_NoPermission_Fail() {
        // given
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doThrow(new CustomException(ErrorCode.NOT_LEADER))
                .when(userMeetingService).verifyLeaderPermission(meeting);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                meetingLeaderService.deleteMeeting(meeting.getId()));

        assertEquals(ErrorCode.NOT_LEADER, exception.getErrorCode());
    }

}
