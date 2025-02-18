package findme.dangdangcrew.evaluation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import findme.dangdangcrew.evaluation.dto.*;
import findme.dangdangcrew.evaluation.service.EvaluationService;
import findme.dangdangcrew.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import findme.dangdangcrew.global.exception.GlobalExceptionHandler;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EvaluationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EvaluationService evaluationService;

    @InjectMocks
    private EvaluationController evaluationController;

    private EvaluationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(evaluationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        requestDto = EvaluationRequestDto.builder()
                .targetUserId(1L)
                .meetingId(1L)
                .score(5)
                .comment("강아지 이뻐요")
                .build();
    }

    @Test
    @DisplayName("평가 생성 테스트")
    void createEvaluationTest() throws Exception {
        EvaluationCreateResponseDto responseDto = EvaluationCreateResponseDto.builder()
                .evaluationId(1L)
                .message("평가가 성공적으로 작성되었습니다.")
                .build();

        when(evaluationService.createEvaluation(any(EvaluationRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.evaluationId").value(1L))
                .andExpect(jsonPath("$.data.message").value("평가가 성공적으로 작성되었습니다."));
    }

    @Test
    @DisplayName("내가 작성한 평가 조회")
    void getWrittenEvaluationsTest() throws Exception {
        WrittenEvaluationsResponseDto responseDto = WrittenEvaluationsResponseDto.builder()
                .evaluatorId(1L)
                .evaluations(List.of())
                .build();

        when(evaluationService.getEvaluationsByEvaluator()).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/evaluations/written"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.evaluatorId").value(1L));
    }

    @Test
    @DisplayName("내가 받은 평가 조회")
    void getReceivedEvaluationsTest() throws Exception {
        ReceivedEvaluationsResponseDto responseDto = ReceivedEvaluationsResponseDto.builder()
                .targetUserId(1L)
                .averageScore(4.5)
                .evaluations(List.of())
                .build();

        when(evaluationService.getEvaluationsByUser()).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/evaluations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.averageScore").value(4.5));
    }
}