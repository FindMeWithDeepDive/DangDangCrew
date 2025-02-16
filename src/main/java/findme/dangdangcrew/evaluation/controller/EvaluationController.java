package findme.dangdangcrew.evaluation.controller;

import findme.dangdangcrew.evaluation.dto.*;
import findme.dangdangcrew.evaluation.service.EvaluationService;
import findme.dangdangcrew.global.dto.ResponseDto;
import findme.dangdangcrew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
public class EvaluationController {
    private final EvaluationService evaluationService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDto<EvaluationCreateResponseDto>> createEvaluation(
            @RequestBody EvaluationRequestDto requestDto) {
        Long evaluatorId = userService.getCurrentUser().getId();
        EvaluationCreateResponseDto response = evaluationService.createEvaluation(evaluatorId, requestDto);
        return ResponseEntity.ok(ResponseDto.of(response, "평가가 성공적으로 작성되었습니다."));
    }

    @GetMapping("/written")
    public ResponseEntity<ResponseDto<WrittenEvaluationsResponseDto>> getWrittenEvaluations() {
        Long evaluatorId = userService.getCurrentUser().getId();
        WrittenEvaluationsResponseDto response = evaluationService.getEvaluationsByEvaluator(evaluatorId);
        return ResponseEntity.ok(ResponseDto.of(response, "내가 작성한 평가 조회 성공"));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<ReceivedEvaluationsResponseDto>> getEvaluationsByUser() {
        Long userId = userService.getCurrentUser().getId();
        ReceivedEvaluationsResponseDto response = evaluationService.getEvaluationsByUser(userId);
        return ResponseEntity.ok(ResponseDto.of(response, "평가 정보 조회 성공"));
    }
}
