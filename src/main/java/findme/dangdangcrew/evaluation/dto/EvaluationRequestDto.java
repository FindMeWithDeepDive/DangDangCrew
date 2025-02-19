package findme.dangdangcrew.evaluation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class EvaluationRequestDto {
    private Long targetUserId;
    private Long meetingId;
    private int score;
    private String comment;

    public EvaluationRequestDto(Long targetUserId, Long meetingId, int score, String comment) {
        this.targetUserId = targetUserId;
        this.meetingId = meetingId;
        this.score = score;
        this.comment = comment;
    }
}