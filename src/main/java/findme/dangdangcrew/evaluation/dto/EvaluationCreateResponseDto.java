package findme.dangdangcrew.evaluation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EvaluationCreateResponseDto {
    private Long evaluationId;
    private String message;
}