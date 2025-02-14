package findme.dangdangcrew.user.controller;

import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.user.dto.*;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class); // ✅ Logger 선언 추가
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "유저를 등록합니다.")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.registerUser(userRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력받아 JWT 토큰을 발급합니다.")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Redis에 저장된 Refresh Token과 비교하여 검증 후 새로운 Access Token을 발급합니다.")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(@RequestBody TokenRefreshRequestDto request) {
        TokenResponseDto response = userService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃을 수행하고 Refresh Token을 삭제합니다.")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "JWT 토큰을 기반으로 본인 정보를 조회합니다.")
    public ResponseEntity<UserResponseDto> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @GetMapping("/me/entity")
    @Operation(summary = "내 엔티티 조회", description = "JWT 토큰을 기반으로 본인 `User` 엔티티 정보를 반환합니다.")
    public ResponseEntity<User> getCurrentUserEntity() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "로그인 상태에서 비밀번호를 변경합니다.")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

}
