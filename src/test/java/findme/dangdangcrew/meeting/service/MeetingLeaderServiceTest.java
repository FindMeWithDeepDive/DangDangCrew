package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.meeting.MeetingTestData;
import findme.dangdangcrew.meeting.dto.MeetingDetailResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingRequestDto;
import findme.dangdangcrew.meeting.dto.MeetingUserResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingUserStatusUpdateRequestDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.mapper.MeetingMapper;
import findme.dangdangcrew.meeting.repository.MeetingRepository;
import findme.dangdangcrew.notification.event.LeaderActionEvent;
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

import java.util.List;
import java.util.Optional;

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
    private MeetingRepository meetingRepository;

    @Mock
    private EventPublisher eventPublisher;

    private Meeting meeting;
    private User leader;
    private User applicant;
    private UserMeeting userMeeting;
    private Place place;
    private MeetingUserResponseDto meetingUserResponseDto;
    private MeetingUserStatusUpdateRequestDto meetingUserStatusUpdateRequestDto;
    private MeetingUserStatusUpdateRequestDto meetingUserStatusUpdateRequestDto_Cancelled;
    private MeetingDetailResponseDto changeMeetingDetailResponseDto;
    private MeetingRequestDto changeMeetingRequestDto;

    @BeforeEach
    void setup() {
        leader = MeetingTestData.createUser();
        applicant = MeetingTestData.createUser1();
        place = MeetingTestData.createPlace();
        meeting = MeetingTestData.createMeeting(place);
        userMeeting = MeetingTestData.createUserMeeting(leader, meeting);
        meetingUserResponseDto = MeetingTestData.createMeetingUserResponseDto(userMeeting);
        meetingUserStatusUpdateRequestDto = MeetingTestData.createMeetingUserStatusUpdateRequestDto();
        meetingUserStatusUpdateRequestDto_Cancelled = MeetingTestData.createMeetingUserStatusUpdateRequestDto_Cancelled();
        changeMeetingDetailResponseDto = createMeetingRequestDto();
        changeMeetingRequestDto = createChangeStatusMeetingRequestDto();
    }

    private MeetingDetailResponseDto createMeetingRequestDto() {
        return MeetingDetailResponseDto.builder()
                .meetingName("수정된 모임")
                .information("수정된 모임입니다.")
                .maxPeople(10)
                .build();
    }

    private MeetingRequestDto createChangeStatusMeetingRequestDto(){
        return MeetingRequestDto.builder()
                .meetingName("수정된 모임")
                .information("수정된 모임입니다.")
                .maxPeople(2)
                .placeRequestDto(MeetingTestData.createPlaceRequestDto())
                .build();
    }

    @Test
    @DisplayName("[✅모임 신청 상태 변경] - 리더가 신청자의 상태를 성공적으로 변경합니다.")
    void changeMeetingApplicationStatusByLeader_Accept(){
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userService.getUser(applicant.getId())).thenReturn(applicant);
        when(userMeetingService.getUserMeeting(meeting, applicant)).thenReturn(userMeeting);
        when(userMeetingService.updateMeetingStatus(meeting, applicant, UserMeetingStatus.CONFIRMED)).thenReturn(userMeeting);
        when(meetingMapper.toUserDto(userMeeting)).thenReturn(meetingUserResponseDto);

        MeetingUserResponseDto response = meetingLeaderService.changeMeetingApplicationStatusByLeader(meeting.getId(), meetingUserStatusUpdateRequestDto);

        assertNotNull(response);
        verify(meetingService, times(1)).findProgressMeeting(meeting.getId());
        verify(userMeetingService, times(1)).updateMeetingStatus(meeting, applicant, UserMeetingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("[✅모임 신청 승인] - 모임 신청 승인시 현재 인원이 증가해야 합니다.")
    void changeMeetingApplicationStatusByLeader_IncreasePeople() {
        UserMeeting userMeeting = UserMeeting.builder()
                .id(3L)
                .status(UserMeetingStatus.WAITING)
                .meeting(meeting)
                .user(applicant)
                .build();
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userService.getUser(applicant.getId())).thenReturn(applicant);
        when(userMeetingService.getUserMeeting(meeting, applicant)).thenReturn(userMeeting);
        when(userMeetingService.updateMeetingStatus(meeting, applicant, UserMeetingStatus.CONFIRMED)).thenReturn(userMeeting);
        when(meetingMapper.toUserDto(userMeeting)).thenReturn(meetingUserResponseDto);
        doNothing().when(eventPublisher).publisher(any(LeaderActionEvent.class));

        int beforePeople = meeting.getCurPeople();

        MeetingUserResponseDto result = meetingLeaderService.changeMeetingApplicationStatusByLeader(meeting.getId(), meetingUserStatusUpdateRequestDto);

        assertNotNull(result);
        assertEquals(beforePeople + 1, meeting.getCurPeople());
        verify(meetingService, times(1)).updateMeetingStatus(meeting);
    }

    @Test
    @DisplayName("[✅모임 신청 강퇴] - 참가자 강퇴 시 현재 인원이 감소해야 합니다.")
    void changeMeetingApplicationStatusByLeader_DecreasePeople() {
        UserMeeting userMeeting = UserMeeting.builder()
                .id(3L)
                .status(UserMeetingStatus.CONFIRMED)
                .meeting(meeting)
                .user(applicant)
                .build();
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userService.getUser(applicant.getId())).thenReturn(applicant);
        when(userMeetingService.getUserMeeting(meeting, applicant)).thenReturn(userMeeting);
        when(userMeetingService.updateMeetingStatus(meeting, applicant, UserMeetingStatus.CANCELLED)).thenReturn(userMeeting);
        when(meetingMapper.toUserDto(userMeeting)).thenReturn(meetingUserResponseDto);
        doNothing().when(eventPublisher).publisher(any(LeaderActionEvent.class));

        int beforePeople = meeting.getCurPeople();

        MeetingUserResponseDto result = meetingLeaderService.changeMeetingApplicationStatusByLeader(meeting.getId(), meetingUserStatusUpdateRequestDto_Cancelled);

        assertNotNull(result);
        assertEquals(beforePeople - 1, meeting.getCurPeople());
        verify(meetingService, times(1)).updateMeetingStatus(meeting);
    }

    @Test
    @DisplayName("[❌모임 신청 상태 변경] - 리더가 아닌 유저가 모임 신청 상태를 변경하려고 하는 경우 예외를 발생시킵니다.")
    void changeMeetingApplicationStatusByLeader_NotLeader() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doThrow(new CustomException(ErrorCode.NOT_LEADER)).when(userMeetingService).verifyLeaderPermission(meeting);

        CustomException exception = assertThrows(CustomException.class, () ->
                meetingLeaderService.changeMeetingApplicationStatusByLeader(meeting.getId(), meetingUserStatusUpdateRequestDto));

        assertEquals(ErrorCode.NOT_LEADER, exception.getErrorCode());
    }

    @Test
    @DisplayName("[✅ 모임 신청자 조회] - 리더가 모임 신청자들 목록을 성공적으로 조회합니다.")
    void readAllApplications() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userMeetingService.findWaitingByMeetingId(meeting)).thenReturn(List.of(userMeeting));
        when(meetingMapper.toListUserDto(List.of(userMeeting))).thenReturn(List.of(meetingUserResponseDto));

        List<MeetingUserResponseDto> result = meetingLeaderService.readAllApplications(meeting.getId());

        assertEquals(1, result.size());
        verify(meetingService, times(1)).findProgressMeeting(meeting.getId());
    }

    @Test
    @DisplayName("[✅모임 수정] - 리더가 모임을 성공적으로 수정합니다.")
    void updateMeeting() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        doNothing().when(meetingService).validateMeetingCapacity(changeMeetingRequestDto.getMaxPeople());
        when(meetingMapper.toDto(meeting)).thenReturn(changeMeetingDetailResponseDto);

        MeetingDetailResponseDto result = meetingLeaderService.updateMeeting(meeting.getId(), changeMeetingRequestDto);

        assertNotNull(result);
        verify(meetingService, times(1)).findProgressMeeting(meeting.getId());
    }

    @Test
    @DisplayName("[❌모임 수정] - 최대 인원이 2명 이상 10명 이하가 아니라 예외를 발생시킵니다.")
    void updateMeeting_InvalidMaxPeople_Fail() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);

        doThrow(new CustomException(ErrorCode.INVALID_MEETING_CAPACITY))
                .when(meetingService).validateMeetingCapacity(anyInt());

        changeMeetingRequestDto = MeetingRequestDto.builder()
                .meetingName("잘못된 모임")
                .information("테스트")
                .maxPeople(11)
                .placeRequestDto(MeetingTestData.createPlaceRequestDto())
                .build();

        CustomException exception = assertThrows(CustomException.class,
                () -> meetingLeaderService.updateMeeting(meeting.getId(), changeMeetingRequestDto));

        assertEquals(ErrorCode.INVALID_MEETING_CAPACITY, exception.getErrorCode());

        verify(meetingService, times(1)).validateMeetingCapacity(anyInt());
    }

    @Test
    @DisplayName("[✅ 모임 삭제] - 리더가 모임을 성공적으로 삭제합니다.")
    void deleteMeeting() {
        Meeting spyMeeting = spy(meeting);

        when(meetingService.findProgressMeeting(spyMeeting.getId())).thenReturn(spyMeeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(spyMeeting);

        meetingLeaderService.deleteMeeting(spyMeeting.getId());

        verify(userMeetingService, times(1)).delete(spyMeeting);
        verify(spyMeeting, times(1)).updateMeetingStatus(MeetingStatus.DELETED);

        assertEquals(MeetingStatus.DELETED, spyMeeting.getStatus());
    }

    @Test
    @DisplayName("[❌ 모임 삭제] - 해당 모임을 찾지 못해 예외를 발생시킵니다.")
    void deleteMeeting_NotFound_Fail() {
        when(meetingService.findProgressMeeting(999L)).thenThrow(new CustomException(ErrorCode.MEETING_NOT_FOUND));

        CustomException exception = assertThrows(CustomException.class, () -> meetingLeaderService.deleteMeeting(999L));

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    @DisplayName("[✅ 모임 종료] - 리더가 모임을 성공적으로 종료시킵니다.")
    void quitMeeting() {
        when(meetingService.findProgressMeeting(meeting.getId())).thenReturn(meeting);
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userMeetingService.findConfirmedByMeetingId(meeting)).thenReturn(List.of(userMeeting));
        when(meetingMapper.toListMeetingUsersDto(List.of(userMeeting))).thenReturn(List.of(meetingUserResponseDto));

        List<MeetingUserResponseDto> result = meetingLeaderService.quitMeeting(meeting.getId());

        assertEquals(1, result.size());
        verify(meetingService, times(1)).findProgressMeeting(meeting.getId());
    }

    @Test
    @DisplayName("[✅참석/불참 여부 변경] - 리더가 유저의 참석 여부를 성공적으로 변경시킵니다.")
    void checkAttendedOrAbsent_Success() {
        UserMeeting userMeeting = UserMeeting.builder()
                .id(3L)
                .status(UserMeetingStatus.CONFIRMED)
                .meeting(meeting)
                .user(applicant)
                .build();

        when(meetingRepository.findByIdAndStatus(meeting.getId(), MeetingStatus.CLOSED))
                .thenReturn(Optional.of(meeting));
        doNothing().when(userMeetingService).verifyLeaderPermission(meeting);
        when(userMeetingService.findUserMeetingsByMeetingAndUserIds(eq(meeting), anyList()))
                .thenReturn(List.of(userMeeting));
        when(meetingMapper.toListMeetingUsersDto(List.of(userMeeting)))
                .thenReturn(List.of(meetingUserResponseDto));

        List<MeetingUserStatusUpdateRequestDto> dtos = List.of(
                MeetingUserStatusUpdateRequestDto.builder()
                        .userId(applicant.getId())
                        .status(UserMeetingStatus.ATTENDED)
                        .build()
        );

        List<MeetingUserResponseDto> result = meetingLeaderService.checkAttendedOrAbsent(meeting.getId(), dtos);

        assertEquals(1, result.size());
        verify(meetingRepository, times(1)).findByIdAndStatus(meeting.getId(), MeetingStatus.CLOSED);
        verify(userMeetingService, times(1)).verifyLeaderPermission(meeting);
        verify(userMeetingService, times(1)).findUserMeetingsByMeetingAndUserIds(eq(meeting), anyList());
        verify(meetingMapper, times(1)).toListMeetingUsersDto(List.of(userMeeting));
    }
}
