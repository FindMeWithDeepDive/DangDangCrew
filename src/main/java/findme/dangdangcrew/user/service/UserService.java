package findme.dangdangcrew.user.service;

import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.service.RedisService;
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

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    //private final StringRedisTemplate redisTemplate;
    private final RedisService redisService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_IN_USE);
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(userRequestDto.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_IN_USE);
        }

        // 전화번호 중복 검사
        if (userRepository.existsByPhoneNumber(userRequestDto.getPhoneNumber())) {
            throw new CustomException(ErrorCode.PHONE_NUMBER_ALREADY_IN_USE);
        }

        // 비밀번호 길이 검사 (6자리 이상)
        if (userRequestDto.getPassword().length() < 6) {
            throw new CustomException(ErrorCode.WEAK_PASSWORD);
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        //redisTemplate.opsForValue().set("refresh:" + user.getEmail(), refreshToken, 14, TimeUnit.DAYS);
        redisService.saveRefreshToken(user.getEmail(), refreshToken);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public TokenResponseDto refreshAccessToken(TokenRefreshRequestDto request) {
        String refreshToken = request.getRefreshToken();

        // 1.Refresh Token에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // 2.Redis에 저장된 Refresh Token과 비교
        //String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + email);
        String storedRefreshToken = redisService.getRefreshToken(email);

        if (storedRefreshToken == null) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3.새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(email);

        return new TokenResponseDto(newAccessToken, refreshToken);
    }

    public void logout() {
        User user = getCurrentUser();
        redisService.deleteRefreshToken(user.getEmail());
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

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
      
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.MISSING_TOKEN);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails)) {
            throw new CustomException(ErrorCode.INVALID_AUTHENTICATION);
        }

        UserDetails userDetails = (UserDetails) principal;
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDto request) {
        User user = getCurrentUser(); // 현재 로그인한 사용자 정보 가져오기

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        // 새로운 비밀번호 암호화 후 저장
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }
}
