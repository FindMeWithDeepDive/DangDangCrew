package findme.dangdangcrew.meeting.repository;

import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface UserMeetingRepository extends JpaRepository<UserMeeting, Long> {
    Optional<UserMeeting> findFirstByStatusAndMeeting_Id(UserMeetingStatus status, Long meetingId);

    List<UserMeeting> findAllByStatusAndMeeting_Id(UserMeetingStatus status, Long meetingId);

    Optional<UserMeeting> findFirstByMeeting_IdAndUser_Id(Long meetingId, Long userId);

    List<UserMeeting> findAllByMeeting_Id(Long meetingId);

    List<UserMeeting> findAllByUser_Id(Long userId);

    @Modifying
    void deleteAllByStatus(UserMeetingStatus status);
}
