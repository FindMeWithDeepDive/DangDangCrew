package findme.dangdangcrew.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRefreshRequestDto {

    @NotBlank(message = "RefreshToken은 필수 입력 값입니다.")
    private String refreshToken;
}
