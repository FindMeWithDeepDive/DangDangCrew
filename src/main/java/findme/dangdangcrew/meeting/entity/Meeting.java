package findme.dangdangcrew.meeting.entity;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.place.domain.Place;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting")
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String meetingName;

    @Column(nullable = false)
    private String information;

    @Column(nullable = false)
    private Integer maxPeople;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer curPeople = 1;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    @PrePersist // 엔티티 저장 전 기본값 설정
    protected void onCreate() {
        if(this.status == null){
            this.status = MeetingStatus.IN_PROGRESS;
        }
    }

    public void increaseCurPeople() {
        if(this.curPeople <= maxPeople) {
            this.curPeople++;
        } else {
            throw new CustomException(ErrorCode.MEETING_EXCEED_LIMIT_PEOPLE);
        }
    }

    public void decreaseCurPeople() {
        if(this.curPeople > 0){
            this.curPeople--;
        } else {
            throw new CustomException(ErrorCode.MEETING_INSUFFICIENT_PEOPLE);
        }
    }

    public void updateMeeting(String meetingName, String information, Integer maxPeople) {
        this.meetingName = meetingName;
        this.information = information;
        this.maxPeople = maxPeople;
    }

    public void updateMeetingStatus(MeetingStatus status) {
        this.status = status;
    }
}