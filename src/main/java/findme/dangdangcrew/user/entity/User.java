package findme.dangdangcrew.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "nickname"),
        @UniqueConstraint(columnNames = "phone_number")
})public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private BigDecimal userScore = BigDecimal.ZERO;

    public void updateScore(BigDecimal newScore) {
        this.userScore = newScore;
    }

    @CreatedDate
    @Column(updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(); // 명확하게 현재 시간 저장
        }
    }

    @Builder
    public User(String email, String password, String name, String nickname, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}