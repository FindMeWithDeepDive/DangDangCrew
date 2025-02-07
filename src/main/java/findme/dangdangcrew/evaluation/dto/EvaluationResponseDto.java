package findme.dangdangcrew.evaluation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EvaluationResponseDto {
    private double averageScore;
    private List<EvaluationDetail> evaluations;

    @Getter
    @Builder
    public static class EvaluationDetail {
        private Long meetingId;
        private Long evaluatorId;
        private int score;
        private String comment;
        private String createdAt;
    }
}