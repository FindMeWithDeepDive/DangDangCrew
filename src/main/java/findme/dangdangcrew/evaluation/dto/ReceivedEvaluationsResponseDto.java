package findme.dangdangcrew.evaluation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReceivedEvaluationsResponseDto {
    private Long targetUserId;
    private double averageScore;
    private List<EvaluationResponseDto.EvaluationDetail> evaluations;
}