package findme.dangdangcrew.evaluation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
@Getter
@NoArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationId;

    @Column(nullable = false)
    private Long targetUserId;

    @Column(nullable = false)
    private Long evaluatorId;

    @Column(nullable = false)
    private Long meetingId;

    @Column(nullable = false)
    private int score;

    @Column(nullable = true)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Evaluation(Long targetUserId, Long evaluatorId, Long meetingId, int score, String comment) {
        this.targetUserId = targetUserId;
        this.evaluatorId = evaluatorId;
        this.meetingId = meetingId;
        this.score = score;
        this.comment = comment;
    }
}