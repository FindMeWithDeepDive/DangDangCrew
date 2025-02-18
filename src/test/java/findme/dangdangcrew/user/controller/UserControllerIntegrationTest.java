package findme.dangdangcrew.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import findme.dangdangcrew.global.service.RedisService;
import findme.dangdangcrew.user.dto.*;
import findme.dangdangcrew.user.repository.UserRepository;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // H2 환경 설정 적용
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RedisService redisService;

    private final String TEST_EMAIL = "newuser@example.com";
    private final String PASSWORD = "password123";

    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        userRepository.flush(); // 강제 반영

        redisService = Mockito.mock(RedisService.class);
        ReflectionTestUtils.setField(userService, "redisService", redisService);

        // 회원가입 및 로그인 수행
        registerUser(TEST_EMAIL, PASSWORD);
        loginUser(TEST_EMAIL, PASSWORD);
    }

    private void registerUser(String email, String password) throws Exception {
        UserRequestDto requestDto = new UserRequestDto(email, password, "Jimin", "nickname", "01012345678");

        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    private void loginUser(String email, String password) throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto(email, password);
        String responseBody = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenResponseDto tokenResponse = objectMapper.readValue(responseBody, TokenResponseDto.class);
        this.accessToken = tokenResponse.getAccessToken();
        this.refreshToken = tokenResponse.getRefreshToken();
    }



    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        when(redisService.getRefreshToken(TEST_EMAIL)).thenReturn(refreshToken);

        TokenRefreshRequestDto request = new TokenRefreshRequestDto(refreshToken);
        mockMvc.perform(post("/api/v1/users/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void shouldLogoutUserSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/users/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        verify(redisService, times(1)).deleteRefreshToken(TEST_EMAIL);
    }

    @Test
    void shouldGetUserInfoSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.name").value("Jimin"));
    }

    @Test
    void shouldChangePasswordSuccessfully() throws Exception {
        ChangePasswordRequestDto request = new ChangePasswordRequestDto(PASSWORD, "newPassword");

        mockMvc.perform(put("/api/v1/users/password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}
