package findme.dangdangcrew.evaluation.repository;

import findme.dangdangcrew.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findAllByTargetUserId(Long targetUserId);
}