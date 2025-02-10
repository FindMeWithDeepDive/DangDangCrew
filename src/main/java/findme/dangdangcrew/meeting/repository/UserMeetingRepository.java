package findme.dangdangcrew.meeting.repository;

import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserMeetingRepository extends JpaRepository<UserMeeting, Long> {
    Optional<UserMeeting> findFirstByStatusAndMeeting_Id(UserMeetingStatus status, Long meetingId);

    List<UserMeeting> findAllByStatusAndMeeting_Id(UserMeetingStatus status, Long meetingId);

    Optional<UserMeeting> findFirstByMeeting_IdAndUser_Id(Long meetingId, Long userId);
}
