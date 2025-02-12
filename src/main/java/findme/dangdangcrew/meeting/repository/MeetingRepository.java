package findme.dangdangcrew.meeting.repository;

import findme.dangdangcrew.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByPlace_Id(String placeId);
}
