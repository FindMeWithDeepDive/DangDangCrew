//package findme.dangdangcrew.user.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import findme.dangdangcrew.global.config.JwtTokenProvider;
//import findme.dangdangcrew.user.dto.*;
//import findme.dangdangcrew.user.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class UserControllerTest {
//
//    private MockMvc mockMvc;
//    private UserService userService;
//    private JwtTokenProvider jwtTokenProvider;
//    private UserController userController;
//
//    @BeforeEach
//    void setUp() {
//        userService = Mockito.mock(UserService.class);
//        jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
//        userController = new UserController(userService, jwtTokenProvider);
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//    }
//
//    @Test
//    void shouldRegisterUserSuccessfully() throws Exception {
//        // given
//        UserRequestDto requestDto = new UserRequestDto("test@example.com", "password123", "Jimin", "nickname", "01012341234");
//        UserResponseDto responseDto = new UserResponseDto(1L, "test@example.com", "Jimin", "nickname", "01012341234", LocalDateTime.now(), new BigDecimal("100"));
//
//        when(userService.registerUser(any(UserRequestDto.class))).thenReturn(responseDto);
//
//        // when & then
//        mockMvc.perform(post("/api/v1/users/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Jimin"));
//    }
//
//    @Test
//    void shouldLoginSuccessfully() throws Exception {
//        // given
//        LoginRequestDto request = new LoginRequestDto("test@example.com", "password123");
//        TokenResponseDto response = new TokenResponseDto("access-token", "refresh-token");
//
//        when(userService.authenticate(any(LoginRequestDto.class))).thenReturn(response);
//
//        // when & then
//        mockMvc.perform(post("/api/v1/users/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken").value("access-token"));
//    }
//
//    @Test
//    void shouldRefreshTokenSuccessfully() throws Exception {
//        // given
//        TokenRefreshRequestDto request = new TokenRefreshRequestDto("valid-refresh-token");
//        TokenResponseDto response = new TokenResponseDto("new-access-token", "valid-refresh-token");
//
//        when(userService.refreshAccessToken(any(TokenRefreshRequestDto.class))).thenReturn(response);
//
//        // when & then
//        mockMvc.perform(post("/api/v1/users/refresh")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
//    }
//
//    @Test
//    void shouldLogoutUserSuccessfully() throws Exception {
//        mockMvc.perform(post("/api/v1/users/logout"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void shouldGetUserInfoSuccessfully() throws Exception {
//        // given
//        UserResponseDto responseDto = new UserResponseDto(1L, "test@example.com", "Jimin", "nickname", "010-1234-5678", null, null);
//        when(userService.getUserInfo()).thenReturn(responseDto);
//
//        // when & then
//        mockMvc.perform(get("/api/v1/users/me")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").value("test@example.com"))
//                .andExpect(jsonPath("$.name").value("Jimin"));
//    }
//
//    @Test
//    void shouldChangePasswordSuccessfully() throws Exception {
//        ChangePasswordRequestDto request = new ChangePasswordRequestDto("oldPassword", "newPassword");
//
//        mockMvc.perform(put("/api/v1/users/password")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(request)))
//                .andExpect(status().isNoContent());
//    }
//}
