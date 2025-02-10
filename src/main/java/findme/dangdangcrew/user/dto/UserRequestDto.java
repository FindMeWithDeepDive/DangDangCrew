package findme.dangdangcrew.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@NoArgsConstructor
public class UserRequestDto {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "user123")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @Schema(description = "사용자 이름", example = "김구름")
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @Schema(description = "사용자 닉네임", example = "구르미")
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @Schema(description = "사용자 전화번호", example = "01011112222")
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자여야 합니다.") // 한국 기준
    private String phoneNumber;
}
