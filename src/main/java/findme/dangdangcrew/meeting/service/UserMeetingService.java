package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.repository.UserMeetingRepository;
import findme.dangdangcrew.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMeetingService {

    private final UserMeetingRepository userMeetingRepository;

    public UserMeeting findUserMeeting(Meeting meeting, User user) {
        return userMeetingRepository.findFirstByMeeting_IdAndUser_Id(meeting.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 이 모임에 속해있지 않습니다."));
    }

    // 유저가 미팅에 참가
    @Transactional
    public UserMeeting saveUserAndMeeting(Meeting meeting, User user, UserMeetingStatus status) {
        UserMeeting userMeeting = UserMeeting.builder()
                .meeting(meeting)
                .user(user)
                .status(status)
                .build();

        return userMeetingRepository.save(userMeeting);
    }

    // 리더 찾기
    public User findLeader(Meeting meeting) {
        UserMeeting userMeeting = userMeetingRepository.findFirstByStatusAndMeeting_Id(UserMeetingStatus.LEADER, meeting.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 모임의 리더를 찾을 수 없습니다."));
        return userMeeting.getUser();
    }

    // 신청 취소 - 일반 유저
    @Transactional
    public UserMeeting cancelMeetingApplication(Meeting meeting, User user) {
        UserMeeting userMeeting = findUserMeeting(meeting, user);
        if(userMeeting.getStatus() == UserMeetingStatus.WAITING) {
            userMeeting.updateStatus(UserMeetingStatus.CANCELLED);
            return userMeeting;
        } else {
            throw new IllegalArgumentException("취소할 수 없습니다.");
        }
    }

    // 신청 상태 변경 - 모임 생성자
    @Transactional
    public UserMeeting updateMeetingApplication(Meeting meeting, User user, UserMeetingStatus status) {
        UserMeeting userMeeting = findUserMeeting(meeting, user);
        userMeeting.updateStatus(status);
        return userMeeting;
    }

    // 모임 신청자 전체 조회 - 모임 생성자
    public List<UserMeeting> findWaitingByMeetingId(Meeting meeting){
        return userMeetingRepository.findAllByStatusAndMeeting_Id(UserMeetingStatus.WAITING, meeting.getId());
    }

    // 모임 확정자 전제 조회 - 모임 생성자
    public List<UserMeeting> findConfirmedByMeetingId(Meeting meeting){
        return userMeetingRepository.findAllByStatusAndMeeting_Id(UserMeetingStatus.CONFIRMED, meeting.getId());
    }

    // 모임 삭제시 연관관계 삭제
    public void delete(Meeting meeting) {
        List<UserMeeting> userMeetings = userMeetingRepository.findAllByMeeting_Id(meeting.getId());
        userMeetings.forEach(userMeeting -> userMeeting.updateStatus(UserMeetingStatus.MEETING_DELETED));
    }
}
