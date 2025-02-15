package findme.dangdangcrew.meeting.repository;

import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByPlace_IdAndStatusIn(String placeId, List<MeetingStatus> statuses);
    Optional<Meeting> findByIdAndStatusIn(Long id, List<MeetingStatus> status);
    List<Meeting> findAllByIdIn(List<Long> meetingIds);
}
