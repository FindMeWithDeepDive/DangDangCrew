package findme.dangdangcrew.user.service;

import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.user.controller.UserController;
import findme.dangdangcrew.user.dto.*;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(userRequestDto.getNickname())) {
            throw new IllegalArgumentException("Nickname already in use");
        }

        // 전화번호 중복 검사
        if (userRepository.existsByPhoneNumber(userRequestDto.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already in use");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());

        // 사용자 저장
        User user = User.builder()
                .email(userRequestDto.getEmail())
                .password(encodedPassword)
                .name(userRequestDto.getName())
                .nickname(userRequestDto.getNickname())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .build();
        userRepository.save(user);

        return convertToDto(user);
    }

    public TokenResponseDto authenticate(LoginRequestDto request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        redisTemplate.opsForValue().set("refresh:" + user.getEmail(), refreshToken, 14, TimeUnit.DAYS);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public TokenResponseDto refreshAccessToken(TokenRefreshRequestDto request) {
        String refreshToken = request.getRefreshToken();

        // 1.Refresh Token에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // 2.Redis에 저장된 Refresh Token과 비교
        String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + email);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token. Please log in again.");
        }

        // 3.새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(email);

        return new TokenResponseDto(newAccessToken, refreshToken);
    }

    public void logout() {
        User user = getCurrentUser();
        redisTemplate.delete("refresh:" + user.getEmail());
    }

    public UserResponseDto getUserInfo() {
        User user = getCurrentUser(); // SecurityContext에서 현재 로그인한 유저 가져오기
        return convertToDto(user);
    }

    private UserResponseDto convertToDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUserScore()
        );
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails)) {
            throw new RuntimeException("Authentication principal is not UserDetails type");
        }

        UserDetails userDetails = (UserDetails) principal;
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
