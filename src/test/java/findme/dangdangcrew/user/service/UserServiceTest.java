package findme.dangdangcrew.user.service;

import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.global.service.RedisService;
import findme.dangdangcrew.user.dto.*;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private UserService userService;

    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "encodedPassword";

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
        redisService = Mockito.mock(RedisService.class);
        userService = new UserService(userRepository, passwordEncoder, jwtTokenProvider, redisService);

        // Mock User 엔티티 생성
        User mockUser = new User(TEST_EMAIL, TEST_PASSWORD, "Jimin", "nickname", "01012345678");

        // SecurityContext에 UserDetails 기반 인증 객체 추가
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                mockUser.getEmail(),
                mockUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // given
        UserRequestDto requestDto = new UserRequestDto(TEST_EMAIL, "password123", "Jimin", "nickname", "01012345678");
        User user = new User(TEST_EMAIL, passwordEncoder.encode("password123"), "Jimin", "nickname", "01012345678");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(requestDto.getNickname())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(requestDto.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        UserResponseDto response = userService.registerUser(requestDto);

        // then
        assertNotNull(response);
        assertEquals("Jimin", response.getName());
        assertEquals("nickname", response.getNickname());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        // given
        LoginRequestDto loginRequest = new LoginRequestDto(TEST_EMAIL, "password123");
        User user = new User(TEST_EMAIL, TEST_PASSWORD, "Jimin", "nickname", "01012345678");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(user.getEmail())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(user.getEmail())).thenReturn("refresh-token");

        // when
        TokenResponseDto tokens = userService.authenticate(loginRequest);

        // then
        assertNotNull(tokens);
        assertEquals("access-token", tokens.getAccessToken());
        assertEquals("refresh-token", tokens.getRefreshToken());
    }

    @Test
    void shouldRefreshAccessTokenSuccessfully() {
        // given
        TokenRefreshRequestDto refreshRequest = new TokenRefreshRequestDto("valid-refresh-token");

        when(jwtTokenProvider.getEmailFromToken(refreshRequest.getRefreshToken())).thenReturn(TEST_EMAIL);
        when(redisService.getRefreshToken(TEST_EMAIL)).thenReturn("valid-refresh-token");
        when(jwtTokenProvider.generateAccessToken(TEST_EMAIL)).thenReturn("new-access-token");

        // when
        TokenResponseDto response = userService.refreshAccessToken(refreshRequest);

        // then
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
    }

    @Test
    void shouldLogoutUserSuccessfully() {
        // given
        User user = new User(TEST_EMAIL, TEST_PASSWORD, "Jimin", "nickname", "01012345678");

        // userRepository에서 유저 조회가 가능하도록 Stub 추가
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        // when
        userService.logout();

        // then
        verify(redisService, times(1)).deleteRefreshToken(TEST_EMAIL);
    }

    @Test
    void shouldGetUserInfoSuccessfully() {
        // given
        User user = new User(TEST_EMAIL, TEST_PASSWORD, "Jimin", "nickname", "01012345678");

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        // when
        UserResponseDto response = userService.getUserInfo();

        // then
        assertNotNull(response);
        assertEquals(TEST_EMAIL, response.getEmail());
        assertEquals("Jimin", response.getName());
    }


    @Test
    void shouldChangePasswordSuccessfully() {
        // given
        ChangePasswordRequestDto requestDto = new ChangePasswordRequestDto("oldPassword", "newPassword");
        User user = new User(TEST_EMAIL, TEST_PASSWORD, "Jimin", "nickname", "01012345678");

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(requestDto.getNewPassword())).thenReturn("newEncodedPassword");

        // when
        userService.changePassword(requestDto);

        // then
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(passwordEncoder, times(1)).encode(requestDto.getNewPassword());
    }
}
