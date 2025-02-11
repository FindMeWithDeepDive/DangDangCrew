package findme.dangdangcrew.user.controller;

import findme.dangdangcrew.user.dto.*;
import findme.dangdangcrew.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

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

}
