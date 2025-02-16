package findme.dangdangcrew.evaluation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WrittenEvaluationsResponseDto {
    private Long evaluatorId;
    private List<EvaluationResponseDto.EvaluationDetail> evaluations;
}
