package findme.dangdangcrew.evaluation.service;

import findme.dangdangcrew.evaluation.dto.*;
import findme.dangdangcrew.evaluation.entity.Evaluation;
import findme.dangdangcrew.evaluation.repository.EvaluationRepository;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private EvaluationService evaluationService;

    private Evaluation evaluation;
    private EvaluationRequestDto requestDto;
    private User evaluator;
    private User targetUser;

    @BeforeEach
    void setUp() {
        requestDto = EvaluationRequestDto.builder()
                .targetUserId(1L)
                .meetingId(1L)
                .score(5)
                .comment("강아지 이뻐요")
                .build();

        evaluation = new Evaluation(1L, 2L, 1L, 5, "강아지 이뻐요");
        ReflectionTestUtils.setField(evaluation, "evaluationId", 1L);

        evaluator = new User("evaluator@example.com", "password", "Evaluator", "nickname1", "010-1234-5678");
        targetUser = new User("target@example.com", "password", "Target", "nickname2", "010-8765-4321");

        ReflectionTestUtils.setField(evaluator, "id", 2L);
        ReflectionTestUtils.setField(targetUser, "id", 1L);
    }

    @Test
    @DisplayName("평가 생성 성공 테스트")
    void createEvaluationTest() {
        when(userService.getCurrentUser()).thenReturn(evaluator);
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(invocation -> {
            Evaluation savedEvaluation = invocation.getArgument(0);
            if (savedEvaluation.getEvaluationId() == null) {
                ReflectionTestUtils.setField(savedEvaluation, "evaluationId", 1L);
            }
            return savedEvaluation;
        });

        EvaluationCreateResponseDto response = evaluationService.createEvaluation(requestDto);

        assertNotNull(response);
        assertEquals(1L, response.getEvaluationId());
        assertEquals("평가가 성공적으로 작성되었습니다.", response.getMessage());

        verify(evaluationRepository, times(1)).save(any(Evaluation.class));
    }


    @Test
    @DisplayName("본인 평가 금지 테스트")
    void createEvaluationSelfFailureTest() {
        when(userService.getCurrentUser()).thenReturn(targetUser);

        CustomException exception = assertThrows(CustomException.class, () -> evaluationService.createEvaluation(requestDto));
        assertEquals(ErrorCode.SELF_EVALUATION_NOT_ALLOWED, exception.getErrorCode());
        assertEquals("자기 자신을 평가할 수 없습니다.", exception.getMessage());
    }
}