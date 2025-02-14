package findme.dangdangcrew.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePasswordRequestDto {
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    private String newPassword;
}