package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.global.service.RedisService;
import findme.dangdangcrew.meeting.dto.MeetingUserResponseDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.mapper.MeetingMapper;
import findme.dangdangcrew.notification.event.ApplyEvent;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingUserService {

    private final MeetingService meetingService;
    private final UserService userService;
    private final UserMeetingService userMeetingService;
    private final MeetingMapper meetingMapper;
    private final EventPublisher eventPublisher;
    private final RedisService redisService;

    // 모임 참가 신청
    @Transactional
    public MeetingUserResponseDto applyMeetingByMeetingId(Long id) {
        Meeting meeting = meetingService.findProgressMeeting(id);
        User user = userService.getCurrentUser();
        userMeetingService.isUserAlreadyInMeeting(meeting, user);

        Place place = meeting.getPlace();

        UserMeeting userMeeting = userMeetingService.createUserMeeting(meeting, user, UserMeetingStatus.WAITING);
        User leader = userMeetingService.findLeader(meeting);

        eventPublisher.publisher(new ApplyEvent(leader.getId(), user.getNickname(), user.getId(), meeting.getId(), meeting.getMeetingName()));
        redisService.incrementHotPlace(place.getId());
        return meetingMapper.toUserDto(userMeeting);
    }

    // 모임 참가 취소
    @Transactional
    public MeetingUserResponseDto cancelMeetingApplication(Long id) {
        Meeting meeting = meetingService.findProgressMeeting(id);
        User user = userService.getCurrentUser();

        userMeetingService.verifyUser(meeting, user);
        UserMeeting userMeeting = userMeetingService.updateMeetingStatus(meeting, user, UserMeetingStatus.CANCELLED);
        meetingService.updateMeetingStatus(meeting);

        return meetingMapper.toUserDto(userMeeting);
    }
}
