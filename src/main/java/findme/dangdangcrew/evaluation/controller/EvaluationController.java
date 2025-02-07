package findme.dangdangcrew.evaluation.controller;

import findme.dangdangcrew.evaluation.dto.EvaluationRequestDto;
import findme.dangdangcrew.evaluation.dto.EvaluationResponseDto;
import findme.dangdangcrew.evaluation.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/{evaluatorId}")
    public ResponseEntity<String> createEvaluation(
            @PathVariable Long evaluatorId,
            @RequestBody EvaluationRequestDto requestDto) {
        evaluationService.createEvaluation(evaluatorId, requestDto);
        return ResponseEntity.ok("평가가 성공적으로 작성되었습니다.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<EvaluationResponseDto> getEvaluationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByUser(userId));
    }
}