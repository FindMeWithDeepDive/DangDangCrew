package findme.dangdangcrew.evaluation.dto;

import lombok.Getter;

@Getter
public class EvaluationRequestDto {
    private Long targetUserId;
    private Long meetingId;
    private int score;
    private String comment;
}