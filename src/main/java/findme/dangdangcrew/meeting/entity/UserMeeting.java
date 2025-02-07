package findme.dangdangcrew.meeting.entity;

import findme.dangdangcrew.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user_meeting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userMeetingId;

    @ManyToOne
    private Meeting meeting;

    @ManyToOne
    private User user;
}