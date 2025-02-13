package findme.dangdangcrew.meeting.entity;

import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "user_meeting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private UserMeetingStatus status;

    @PrePersist // 엔티티 저장 전 기본값 설정
    protected void onCreate() {
        if(this.status == null){
            this.status = UserMeetingStatus.WAITING;
        }
    }

    public void updateStatus(UserMeetingStatus newStatus) {
        this.status = newStatus;
    }
}