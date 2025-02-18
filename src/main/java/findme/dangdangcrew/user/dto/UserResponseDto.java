package findme.dangdangcrew.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private BigDecimal userScore;
}
