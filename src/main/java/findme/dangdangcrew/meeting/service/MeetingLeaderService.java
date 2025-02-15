package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.chat.service.ChatRoomService;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.publisher.EventPublisher;
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
import findme.dangdangcrew.notification.event.NewMeetingEvent;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.service.PlaceService;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingLeaderService {

    private final MeetingService meetingService;
    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;
    private final UserService userService;
    private final UserMeetingService userMeetingService;

    // 모임 신청 상태 변경
    @Transactional
    public MeetingUserResponseDto changeMeetingApplicationStatusByLeader(Long id, MeetingUserStatusUpdateRequestDto dto) {
        Meeting meeting = meetingService.findProgressMeeting(id);

        userMeetingService.verifyLeaderPermission(meeting);
        User changeUser = userService.getUser(dto.getUserId());
        UserMeeting userMeeting = userMeetingService.getUserMeeting(meeting, changeUser);
        UserMeetingStatus currentStatus = userMeeting.getStatus();
        UserMeetingStatus newStatus = dto.getStatus();

        if (currentStatus == UserMeetingStatus.WAITING && newStatus == UserMeetingStatus.CONFIRMED) {
            meeting.increaseCurPeople();
        } else if (currentStatus == UserMeetingStatus.CONFIRMED && newStatus == UserMeetingStatus.CANCELLED) {
            meeting.decreaseCurPeople();
        }
        meetingService.updateMeetingStatus(meeting);
        userMeeting = userMeetingService.updateMeetingStatus(meeting, changeUser, dto.getStatus());
        return meetingMapper.toUserDto(userMeeting);
    }

    // 모임 신청자 조회
    public List<MeetingUserResponseDto> readAllApplications(Long id) {
        Meeting meeting = meetingService.findProgressMeeting(id);

        userMeetingService.verifyLeaderPermission(meeting);

        List<UserMeeting> userMeetings = userMeetingService.findWaitingByMeetingId(meeting);
        return meetingMapper.toListUserDto(userMeetings);

    }

    // 모임 수정
    @Transactional
    public MeetingDetailResponseDto updateMeeting(Long id, MeetingRequestDto meetingRequestDto) {
        Meeting meeting = meetingService.findProgressMeeting(id);
        userMeetingService.verifyLeaderPermission(meeting);
        meeting.updateMeeting(
                meetingRequestDto.getMeetingName(),
                meetingRequestDto.getInformation(),
                meetingRequestDto.getMaxPeople()
        );
        return meetingMapper.toDto(meeting);
    }

    // 모임 삭제
    @Transactional
    public void deleteMeeting(Long id) {
        Meeting meeting = meetingService.findProgressMeeting(id);
        userMeetingService.verifyLeaderPermission(meeting);
        meeting.updateMeetingStatus(MeetingStatus.DELETED);
        userMeetingService.delete(meeting);
    }

    // 모임 상태를 종료로 변경
    @Transactional
    public List<MeetingUserResponseDto> quitMeeting(Long id) {
        Meeting meeting = meetingService.findProgressMeeting(id);
        userMeetingService.verifyLeaderPermission(meeting);
        meeting.updateMeetingStatus(MeetingStatus.CLOSED);

        List<UserMeeting> userMeetings = userMeetingService.findConfirmedByMeetingId(meeting);
        return meetingMapper.toListMeetingUsersDto(userMeetings);
    }

    // 참석/불참 여부 변경
    @Transactional
    public List<MeetingUserResponseDto> checkAttendedOrAbsent(Long id, List<MeetingUserStatusUpdateRequestDto> dtos) {
        Meeting meeting = meetingRepository.findByIdAndStatus(id, MeetingStatus.CLOSED).orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
        userMeetingService.verifyLeaderPermission(meeting);

        List<Long> userIds = dtos.stream().map(MeetingUserStatusUpdateRequestDto::getUserId).toList();
        List<UserMeeting> userMeetings = userMeetingService.findUserMeetingsByMeetingAndUserIds(meeting, userIds);

        for (UserMeeting userMeeting : userMeetings) {
            MeetingUserStatusUpdateRequestDto dto = dtos.stream()
                    .filter(d -> d.getUserId().equals(userMeeting.getUser().getId()))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            userMeeting.updateStatus(dto.getStatus());
        }

        return meetingMapper.toListMeetingUsersDto(userMeetings);
    }
}
