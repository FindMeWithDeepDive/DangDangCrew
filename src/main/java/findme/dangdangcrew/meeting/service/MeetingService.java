package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.chat.service.ChatRoomService;
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
    private final PlaceService placeService;
    private final ChatRoomService chatRoomService;
    private final EventPublisher eventPublisher;

    public Meeting findProgressMeeting(Long id) {
        return meetingRepository.findByIdAndStatusIn(id, List.of(MeetingStatus.IN_PROGRESS, MeetingStatus.COMPLETED)).orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }

    // 모임 상세 조회
    public MeetingDetailResponseDto readByMeetingId(Long id) {
        Meeting meeting = findProgressMeeting(id);
        return meetingMapper.toDto(meeting);
    }

    public void updateMeetingStatus(Meeting meeting) {
        meeting.updateMeetingStatus(meeting.getCurPeople().equals(meeting.getMaxPeople()) ? MeetingStatus.COMPLETED : MeetingStatus.IN_PROGRESS);
    }

    // 모임 확정자 조회
    public List<MeetingUserResponseDto> readAllConfirmed(Long id) {
        Meeting meeting = findProgressMeeting(id);
        List<UserMeeting> userMeetings = userMeetingService.findConfirmedByMeetingId(meeting);
        return meetingMapper.toListUserDto(userMeetings);
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

    // 모임 생성
    @Transactional
    public MeetingUserResponseDto create(MeetingRequestDto meetingRequestDto) {
        Place place = placeService.findOrCreatePlace(meetingRequestDto.getPlaceRequestDto());
        validateMeetingCapacity(meetingRequestDto.getMaxPeople());
        Meeting meeting = meetingMapper.toEntity(meetingRequestDto, place);
        meeting = meetingRepository.save(meeting);
        chatRoomService.createChatRoom(meeting);
        eventPublisher.publisher(new NewMeetingEvent(place.getPlaceName(), meeting.getId(), place.getId()));

        User user = userService.getCurrentUser();
        UserMeeting userMeeting = userMeetingService.createUserMeeting(meeting, user, UserMeetingStatus.LEADER);

        return meetingMapper.toUserDto(userMeeting);
    }

    public void validateMeetingCapacity(int maxPeople) {
        if (maxPeople < 2 || maxPeople > 10) {
            throw new CustomException(ErrorCode.INVALID_MEETING_CAPACITY);
        }
    }
}