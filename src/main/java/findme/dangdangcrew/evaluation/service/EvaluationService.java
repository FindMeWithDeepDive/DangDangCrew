package findme.dangdangcrew.evaluation.service;

import findme.dangdangcrew.evaluation.dto.*;
import findme.dangdangcrew.evaluation.entity.Evaluation;
import findme.dangdangcrew.evaluation.repository.EvaluationRepository;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.user.repository.UserRepository;
import findme.dangdangcrew.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public EvaluationService(EvaluationRepository evaluationRepository, UserRepository userRepository, UserService userService) {
        this.evaluationRepository = evaluationRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public EvaluationCreateResponseDto createEvaluation(EvaluationRequestDto requestDto) {
        Long evaluatorId = userService.getCurrentUser().getId();

        if (evaluatorId.equals(requestDto.getTargetUserId())) {
            throw new CustomException(ErrorCode.SELF_EVALUATION_NOT_ALLOWED);
        }

        Evaluation evaluation = new Evaluation(
                requestDto.getTargetUserId(),
                evaluatorId,
                requestDto.getMeetingId(),
                requestDto.getScore(),
                requestDto.getComment()
        );
        evaluationRepository.save(evaluation);

        Double newAverageScore = evaluationRepository.findAverageScoreByUserId(requestDto.getTargetUserId());
        if (newAverageScore == null) {
            newAverageScore = 0.0;
        }
        userRepository.updateUserScore(requestDto.getTargetUserId(), newAverageScore);


        return EvaluationCreateResponseDto.builder()
                .evaluationId(evaluation.getEvaluationId())
                .message("평가가 성공적으로 작성되었습니다.")
                .build();
    }

    public WrittenEvaluationsResponseDto getEvaluationsByEvaluator() {
        Long evaluatorId = userService.getCurrentUser().getId();
        List<Evaluation> evaluations = evaluationRepository.findAllByEvaluatorId(evaluatorId);

        List<EvaluationResponseDto.EvaluationDetail> evaluationDetails = evaluations.stream()
                .map(e -> EvaluationResponseDto.EvaluationDetail.builder()
                        .meetingId(e.getMeetingId())
                        .evaluatorId(e.getEvaluatorId())
                        .score(e.getScore())
                        .comment(e.getComment())
                        .createdAt(e.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                        .build())
                .collect(Collectors.toList());

        return WrittenEvaluationsResponseDto.builder()
                .evaluatorId(evaluatorId)
                .evaluations(evaluationDetails)
                .build();
    }

    public ReceivedEvaluationsResponseDto getEvaluationsByUser() {
        Long targetUserId = userService.getCurrentUser().getId();
        List<Evaluation> evaluations = evaluationRepository.findAllByTargetUserId(targetUserId);

        double averageScore = evaluations.stream()
                .mapToInt(Evaluation::getScore)
                .average()
                .orElse(0.0);

        List<EvaluationResponseDto.EvaluationDetail> evaluationDetails = evaluations.stream()
                .map(e -> EvaluationResponseDto.EvaluationDetail.builder()
                        .meetingId(e.getMeetingId())
                        .evaluatorId(e.getEvaluatorId())
                        .score(e.getScore())
                        .comment(e.getComment())
                        .createdAt(e.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                        .build())
                .collect(Collectors.toList());

        return ReceivedEvaluationsResponseDto.builder()
                .targetUserId(targetUserId)
                .averageScore(averageScore)
                .evaluations(evaluationDetails)
                .build();
    }
}