package findme.dangdangcrew.evaluation.repository;

import findme.dangdangcrew.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findAllByTargetUserId(Long targetUserId);

    List<Evaluation> findAllByEvaluatorId(Long evaluatorId);

    @Query("SELECT AVG(e.score) FROM Evaluation e WHERE e.targetUserId = :targetUserId")
    Double findAverageScoreByUserId(@Param("targetUserId") Long targetUserId);
}