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
import findme.dangdangcrew.notification.event.NewMeetingEvent;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.service.HotPlaceService;
import findme.dangdangcrew.place.service.PlaceService;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/*
 * TODO
 *  1. when : 장소 도메인 작성 후
 *     what : 연관관계 매핑, 장소별 모임 조회 메소드 작성
 *  2. when : 채팅방 구현이 어느정도 된 후
 *     what : 모임 채팅방 참여 api
 *  3. 내가 참여한 모임 조회 구현
 * */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;
    private final UserMeetingService userMeetingService;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    private final PlaceService placeService;
    private final ChatRoomService chatRoomService;
    private final RedisService redisService;

    public Meeting findProgressMeeting(Long id) {
        return meetingRepository.findByIdAndStatus(id, MeetingStatus.IN_PROGRESS).orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }

    /*
     * TODO
     *  when : 유저 로그인 / 토큰 구현된 후
     *  what : 매개변수 부분 수정
     * */
    // 모임 생성
    @Transactional
    public MeetingApplicationResponseDto create(MeetingRequestDto meetingRequestDto, Long userId) {
        Place place = placeService.findOrCreatePlace(meetingRequestDto.getPlaceRequestDto());
        Meeting meeting = meetingMapper.toEntity(meetingRequestDto, place);
        meeting = meetingRepository.save(meeting);
        chatRoomService.createChatRoom(meeting);
        eventPublisher.publisher(new NewMeetingEvent(place.getPlaceName(),meeting.getId(),place.getId()));

        User user = userRepository.findById(userId).orElseThrow();
        UserMeeting userMeeting = userMeetingService.saveUserAndMeeting(meeting, user, UserMeetingStatus.LEADER);

        return meetingMapper.toApplicationDto(userMeeting);
    }

    // 모임 상세 조회
    public MeetingDetailResponseDto readByMeetingId(Long id) {
        Meeting meeting = findProgressMeeting(id);
        return meetingMapper.toDto(meeting);
    }

    /*
    * TODO
    *  when : 유저 로그인 / 토큰 구현된 후
    *  what : 매개변수 부분 수정
    * */
    // 모임 참가 신청
    @Transactional
    public MeetingApplicationResponseDto applyMeetingByMeetingId(Long id, Long userId) {
        Meeting meeting = findProgressMeeting(id);
        User user = userRepository.findById(userId).orElseThrow();
        Place place = meeting.getPlace();

        UserMeeting userMeeting = userMeetingService.saveUserAndMeeting(meeting, user, UserMeetingStatus.WAITING);
        User leader = userMeetingService.findLeader(meeting);

        eventPublisher.publisher(new ApplyEvent(leader.getId(), user.getNickname(), user.getId(), meeting.getId(), meeting.getMeetingName()));
        redisService.incrementHotPlace(place.getId());
        return meetingMapper.toApplicationDto(userMeeting);
    }

    // 모임 참가 취소
    @Transactional
    public MeetingApplicationResponseDto cancelMeetingApplication(Long id, Long userId) {
        Meeting meeting = findProgressMeeting(id);
        User user = userRepository.findById(userId).orElseThrow();

        UserMeeting userMeeting = userMeetingService.cancelMeetingApplication(meeting, user);

        /*
         * TODO
         *  when : 유저 도메인에 평가점수 추가 + 평가 도메인 작성된 후
         *  what : 확정 후 취소했을 경우 평가 점수 감점
         * */
        return meetingMapper.toApplicationDto(userMeeting);
    }

    // 모임 신청 상태 변경(모임 생성자)
    @Transactional
    public MeetingApplicationResponseDto changeMeetingApplicationStatusByLeader(Long id, MeetingApplicationUpdateRequestDto dto){
        Meeting meeting = findProgressMeeting(id);
        User user = userRepository.findById(dto.getUserId()).orElseThrow();

        /*
         * TODO
         *  1. when : 유저 로그인 / 토큰 구현된 후
         *     what : 로그인 유저가 해당 모임 리더인지 확인
         * */

        UserMeeting userMeeting = userMeetingService.findUserMeeting(meeting, user);
        UserMeetingStatus currentStatus = userMeeting.getStatus();
        UserMeetingStatus newStatus = dto.getStatus();

        if ((currentStatus == UserMeetingStatus.CANCELLED || currentStatus == UserMeetingStatus.WAITING)
                && newStatus == UserMeetingStatus.CONFIRMED) {
            meeting.increaseCurPeople();
        } else if (currentStatus == UserMeetingStatus.CONFIRMED && newStatus == UserMeetingStatus.CANCELLED) {
            meeting.decreaseCurPeople();
        }

        userMeeting = userMeetingService.updateMeetingApplication(meeting, user, dto.getStatus());
        return meetingMapper.toApplicationDto(userMeeting);
    }

    // 모임 신청자 조회
    public List<MeetingApplicationResponseDto> readAllApplications(Long id) {
        Meeting meeting = findProgressMeeting(id);

        /*
         * TODO
         *  when : 유저 로그인 / 토큰 구현된 후
         *  what : 로그인 유저가 해당 모임 리더인지 확인
         * */

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
        List<Meeting> meetings = meetingRepository.findAllByPlace_IdAndStatus(placeId, MeetingStatus.IN_PROGRESS);
        return meetingMapper.toListDto(meetings);
    }

    // 모임 수정
    @Transactional
    public MeetingDetailResponseDto updateMeeting(Long id, MeetingRequestDto meetingRequestDto){
        Meeting meeting = findProgressMeeting(id);

        meeting.updateMeeting(
                meetingRequestDto.getMeetingName(),
                meetingRequestDto.getInformation(),
                meetingRequestDto.getMaxPeople()
        );

        return meetingMapper.toDto(meeting);
    }

    // 모임 삭제
    @Transactional
    public void deleteMeeting(Long id){
        Meeting meeting = findProgressMeeting(id);
        meeting.updateMeetingStatus(MeetingStatus.DELETED);
        userMeetingService.delete(meeting);
    }
}