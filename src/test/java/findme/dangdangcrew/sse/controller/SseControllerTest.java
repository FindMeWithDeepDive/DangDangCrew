package findme.dangdangcrew.sse.controller;

import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;
    private User testUser;

    @BeforeEach
    void init(){
        testUser = User.builder()
                .email("user@gmail.com")
                .name("user")
                .nickname("user")
                .phoneNumber("01011113333")
                .password("test1234")
                .build();
        userRepository.saveAndFlush(testUser);
    }

    private String generateTestToken(User user) {
        return jwtTokenProvider.generateAccessToken(user.getEmail());
    }

    @Test
    @DisplayName("유저가 SSE 알림을 구독합니다.")
    void subscribeTest() throws Exception {
        // given
        String token = generateTestToken(testUser);

        //when
        //then
        mockMvc.perform(get("/api/v1/sse/subscribe")
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE)) // SSE 응답을 기대함
                .andExpectAll(
                        status().isOk(), // HTTP 상태 코드가 200인지 확인
                        content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM_VALUE), // 응답 타입 확인
                        header().exists("Content-Type"), // Content-Type 헤더 확인
                        header().string("Content-Type", "text/event-stream"), // 정확한 헤더 값 확인
                        content().string(containsString("data:")), // Body데이터가 존재하는지 확인
                        content().string(containsString("userId = " + testUser.getId())), // 데이터에 userId 포함 여부 확인
                        content().string(containsString("retry:1000")) // SSE의 재시도 타이밍 확인
                )
                .andDo(print());
    }
}