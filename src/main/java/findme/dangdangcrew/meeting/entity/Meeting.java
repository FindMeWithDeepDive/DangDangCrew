package findme.dangdangcrew.meeting.entity;

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

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;

    public void increaseCurPeople() {
        if(this.curPeople <= maxPeople) {
            this.curPeople++;
        } else {
            throw new IllegalArgumentException("현재 인원은 최대 인원(" + this.maxPeople + "명)을 초과할 수 없습니다.");
        }
    }

    public void decreaseCurPeople() {
        if(this.curPeople > 0){
            this.curPeople--;
        } else {
            throw new IllegalArgumentException("현재 인원은 1명 미만이 될 수 없습니다.");
        }
    }

    public void updateMeeting(String meetingName, String information, Integer maxPeople) {
        this.meetingName = meetingName;
        this.information = information;
        this.maxPeople = maxPeople;
    }
}