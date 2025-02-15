package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.chat.service.ChatRoomService;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.global.service.RedisService;
import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.mapper.MeetingMapper;
import findme.dangdangcrew.meeting.repository.MeetingRepository;
import findme.dangdangcrew.notification.event.ApplyEvent;
import findme.dangdangcrew.notification.event.LeaderActionEvent;
import findme.dangdangcrew.notification.event.LeaderActionType;
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
@Transactional(readOnly = true)
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;
    private final UserMeetingService userMeetingService;
    private final UserService userService;
    private final EventPublisher eventPublisher;
    private final PlaceService placeService;
    private final ChatRoomService chatRoomService;
    private final RedisService redisService;

    public Meeting findProgressMeeting(Long id) {
        return meetingRepository.findByIdAndStatusIn(id, List.of(MeetingStatus.IN_PROGRESS, MeetingStatus.COMPLETED)).orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }

    // 모임 생성
    @Transactional
    public MeetingApplicationResponseDto create(MeetingRequestDto meetingRequestDto) {
        Place place = placeService.findOrCreatePlace(meetingRequestDto.getPlaceRequestDto());
        Meeting meeting = meetingMapper.toEntity(meetingRequestDto, place);
        meeting = meetingRepository.save(meeting);
        chatRoomService.createChatRoom(meeting);
        eventPublisher.publisher(new NewMeetingEvent(place.getPlaceName(), meeting.getId(), place.getId()));

        User user = userService.getCurrentUser();
        UserMeeting userMeeting = userMeetingService.saveUserAndMeeting(meeting, user, UserMeetingStatus.LEADER);

        return meetingMapper.toApplicationDto(userMeeting);
    }

    // 모임 상세 조회
    public MeetingDetailResponseDto readByMeetingId(Long id) {
        Meeting meeting = findProgressMeeting(id);
        return meetingMapper.toDto(meeting);
    }

    // 모임 참가 신청
    @Transactional
    public MeetingApplicationResponseDto applyMeetingByMeetingId(Long id) {
        Meeting meeting = findProgressMeeting(id);
        User user = userService.getCurrentUser();
        Place place = meeting.getPlace();

        UserMeeting userMeeting = userMeetingService.saveUserAndMeeting(meeting, user, UserMeetingStatus.WAITING);
        User leader = userMeetingService.findLeader(meeting);

        eventPublisher.publisher(new ApplyEvent(leader.getId(), user.getNickname(), user.getId(), meeting.getId(), meeting.getMeetingName()));
        redisService.incrementHotPlace(place.getId());
        return meetingMapper.toApplicationDto(userMeeting);
    }

    // 모임 참가 취소
    @Transactional
    public MeetingApplicationResponseDto cancelMeetingApplication(Long id) {
        Meeting meeting = findProgressMeeting(id);
        User user = userService.getCurrentUser();

        UserMeeting userMeeting = userMeetingService.updateMeetingStatus(meeting, user, UserMeetingStatus.CANCELLED);

        updateStatus(meeting);

        return meetingMapper.toApplicationDto(userMeeting);
    }

    // 모임 신청 상태 변경(모임 생성자)
    @Transactional
    public MeetingApplicationResponseDto changeMeetingApplicationStatusByLeader(Long id, MeetingApplicationUpdateRequestDto dto) {
        Meeting meeting = findProgressMeeting(id);

        userMeetingService.checkLeaderPermission(meeting);
        User changeUser = userService.getUser(dto.getUserId());
        UserMeeting userMeeting = userMeetingService.findUserMeeting(meeting, changeUser);
        UserMeetingStatus currentStatus = userMeeting.getStatus();
        UserMeetingStatus newStatus = dto.getStatus();

        LeaderActionType leaderActionType = LeaderActionType.JOIN_REJECTED;
        if (currentStatus == UserMeetingStatus.WAITING && newStatus == UserMeetingStatus.CONFIRMED) {
            meeting.increaseCurPeople();
            leaderActionType = LeaderActionType.JOIN_ACCEPTED;
        } else if (currentStatus == UserMeetingStatus.CONFIRMED && newStatus == UserMeetingStatus.CANCELLED) {
            meeting.decreaseCurPeople();
            leaderActionType = LeaderActionType.KICK_OUT;
        }

        updateStatus(meeting);
        userMeeting = userMeetingService.updateMeetingStatus(meeting, changeUser, dto.getStatus());

        // LeaderAction 이벤트
        eventPublisher.publisher(new LeaderActionEvent(changeUser.getId(),meeting.getId(),meeting.getMeetingName(),leaderActionType));

        return meetingMapper.toApplicationDto(userMeeting);
    }

    private void updateStatus(Meeting meeting) {
        if(meeting.getMaxPeople().equals(meeting.getCurPeople())){
            meeting.updateMeetingStatus(MeetingStatus.COMPLETED);
        } else if(meeting.getCurPeople() < meeting.getMaxPeople()) {
            meeting.updateMeetingStatus(MeetingStatus.IN_PROGRESS);
        }
    }

    // 모임 신청자 조회
    public List<MeetingApplicationResponseDto> readAllApplications(Long id) {
        Meeting meeting = findProgressMeeting(id);

        userMeetingService.checkLeaderPermission(meeting);

        List<UserMeeting> userMeetings = userMeetingService.findWaitingByMeetingId(meeting);
        return meetingMapper.toListApplicationDto(userMeetings);

    }

    // 모임 확정자 조회
    public List<MeetingApplicationResponseDto> readAllConfirmed(Long id) {
        Meeting meeting = findProgressMeeting(id);

        List<UserMeeting> userMeetings = userMeetingService.findConfirmedByMeetingId(meeting);
        return meetingMapper.toListApplicationDto(userMeetings);
    }

    // 장소별 모임 조회
    public List<MeetingBasicResponseDto> findMeetingsByPlaceId(String placeId) {
        List<Meeting> meetings = meetingRepository.findAllByPlace_IdAndStatusIn(placeId, List.of(MeetingStatus.IN_PROGRESS, MeetingStatus.COMPLETED));
        return meetingMapper.toListDto(meetings);
    }

    // 내 모임 조회
    public List<MeetingBasicResponseDto> findMyMeetings(){
        User user = userService.getCurrentUser();
        List<UserMeeting> userMeetings = userMeetingService.findUserMeetingByUser(user);
        List<Long> meetingIds = userMeetings.stream().map(meeting -> meeting.getMeeting().getId()).toList();
        List<Meeting> meetings = meetingRepository.findAllByIdIn(meetingIds);
        return meetingMapper.toListDto(meetings);
    }

    // 모임 수정
    @Transactional
    public MeetingDetailResponseDto updateMeeting(Long id, MeetingRequestDto meetingRequestDto) {
        Meeting meeting = findProgressMeeting(id);
        userMeetingService.checkLeaderPermission(meeting);
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
        Meeting meeting = findProgressMeeting(id);
        userMeetingService.checkLeaderPermission(meeting);
        meeting.updateMeetingStatus(MeetingStatus.DELETED);
        userMeetingService.delete(meeting);
    }
}