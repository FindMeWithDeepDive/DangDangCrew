package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.chat.entity.ChatRoom;
import findme.dangdangcrew.chat.repository.ChatRoomRepository;
import findme.dangdangcrew.chat.service.ChatRoomService;
import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.mapper.MeetingMapper;
import findme.dangdangcrew.meeting.repository.MeetingRepository;
import findme.dangdangcrew.notification.event.ApplyEvent;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.service.PlaceService;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public Meeting findById(Long id) {
        return meetingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 미팅이 존재하지 않습니다."));
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

        User user = userRepository.findById(userId).orElseThrow();
        UserMeeting userMeeting = userMeetingService.saveUserAndMeeting(meeting, user, UserMeetingStatus.LEADER);

        return meetingMapper.toApplicationDto(userMeeting);
    }

    // 모임 상세 조회
    public MeetingDetailResponseDto readByMeetingId(Long id) {
        Meeting meeting = findById(id);
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
        Meeting meeting = findById(id);
        User user = userRepository.findById(userId).orElseThrow();

        UserMeeting userMeeting = userMeetingService.saveUserAndMeeting(meeting, user, UserMeetingStatus.WAITING);
        User leader = userMeetingService.findLeader(meeting);

        eventPublisher.publisher(new ApplyEvent(leader.getId(), user.getNickname(), user.getId(), meeting.getId(), meeting.getMeetingName()));

        return meetingMapper.toApplicationDto(userMeeting);
    }

    // 모임 참가 취소
    @Transactional
    public MeetingApplicationResponseDto cancelMeetingApplication(Long id, Long userId) {
        Meeting meeting = findById(id);
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
        Meeting meeting = findById(id);
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
        Meeting meeting = findById(id);

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
        Meeting meeting = findById(id);

        List<UserMeeting> userMeetings = userMeetingService.findConfirmedByMeetingId(meeting);
        return meetingMapper.toListApplicationDto(userMeetings);
    }

    // 장소별 모임 조회
    public List<MeetingBasicResponseDto> findMeetingsByPlaceId(String placeId) {
        List<Meeting> meetings = meetingRepository.findAllByPlace_Id(placeId);
        return meetingMapper.toListDto(meetings);
    }
}