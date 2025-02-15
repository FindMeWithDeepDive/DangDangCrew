package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.repository.UserMeetingRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMeetingService {

    private final UserMeetingRepository userMeetingRepository;
    private final UserService userService;

    public UserMeeting getUserMeeting(Meeting meeting, User user) {
        return userMeetingRepository.findFirstByMeeting_IdAndUser_Id(meeting.getId(), user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_USER_NOT_EXISTS));
    }

    // 리더 확인
    public void verifyLeaderPermission(Meeting meeting) {
        User user = userService.getCurrentUser();
        if (getUserMeeting(meeting, user).getStatus() != UserMeetingStatus.LEADER) {
            throw new CustomException(ErrorCode.NOT_LEADER);
        }
    }

    // 유저가 미팅에 참가
    @Transactional
    public UserMeeting createUserMeeting(Meeting meeting, User user, UserMeetingStatus status) {
        UserMeeting userMeeting = UserMeeting.builder()
                .meeting(meeting)
                .user(user)
                .status(status)
                .build();

        return userMeetingRepository.save(userMeeting);
    }

    // 리더 찾기
    public User findLeader(Meeting meeting) {
        return userMeetingRepository.findFirstByStatusAndMeeting_Id(UserMeetingStatus.LEADER, meeting.getId())
                .map(UserMeeting::getUser)
                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_LEADER_NOT_FOUND));
    }

    @Transactional
    public UserMeeting updateMeetingStatus(Meeting meeting, User user, UserMeetingStatus newStatus) {
        UserMeeting userMeeting = getUserMeeting(meeting, user);
        if(userMeeting.getStatus() == newStatus) {
            throw new CustomException(ErrorCode.NOT_CHANGE);
        }
        if(userMeeting.getStatus() == UserMeetingStatus.CONFIRMED) {
            user.deductUserScore();
        }
        userMeeting.updateStatus(newStatus);
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
    @Transactional
    public void delete(Meeting meeting) {
        List<UserMeeting> userMeetings = userMeetingRepository.findAllByMeeting_Id(meeting.getId());
        userMeetings.forEach(userMeeting -> userMeeting.updateStatus(UserMeetingStatus.REMOVED));
    }

    public List<UserMeeting> findUserMeetingByUser(User user){
        return userMeetingRepository.findAllByUser_Id(user.getId());
    }

    // 기존 신청 여부 확인
    public boolean isUserAlreadyInMeeting(Meeting meeting, User user) {
        Optional<UserMeeting> userMeeting = userMeetingRepository.findFirstByMeeting_IdAndUser_Id(meeting.getId(), user.getId());
        return userMeeting.isPresent();
    }

    // 스케줄링
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteCancelledApplications(){
        userMeetingRepository.deleteAllByStatus(UserMeetingStatus.CANCELLED);
    }
}
