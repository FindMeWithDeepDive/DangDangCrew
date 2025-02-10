package findme.dangdangcrew.evaluation.service;

import findme.dangdangcrew.evaluation.dto.EvaluationRequestDto;
import findme.dangdangcrew.evaluation.dto.EvaluationResponseDto;
import findme.dangdangcrew.evaluation.entity.Evaluation;
import findme.dangdangcrew.evaluation.repository.EvaluationRepository;
import findme.dangdangcrew.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;

    public EvaluationService(EvaluationRepository evaluationRepository, UserRepository userRepository) {
        this.evaluationRepository = evaluationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createEvaluation(Long evaluatorId, EvaluationRequestDto requestDto) {
        Evaluation evaluation = new Evaluation(
                requestDto.getTargetUserId(),
                evaluatorId,
                requestDto.getMeetingId(),
                requestDto.getScore(),
                requestDto.getComment()
        );
        evaluationRepository.save(evaluation);

        Double newAverageScore = evaluationRepository.findAverageScoreByUserId(requestDto.getTargetUserId());

        if (newAverageScore != null) {
            userRepository.updateUserScore(requestDto.getTargetUserId(), newAverageScore);
        }
    }

    public EvaluationResponseDto getEvaluationsByUser(Long targetUserId) {
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

        return EvaluationResponseDto.builder()
                .averageScore(averageScore)
                .evaluations(evaluationDetails)
                .build();
    }
}