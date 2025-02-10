package findme.dangdangcrew.user.repository;

import findme.dangdangcrew.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying
    @Query("UPDATE User u SET u.userScore = :newScore WHERE u.id = :userId")
    void updateUserScore(@Param("userId") Long userId, @Param("newScore") Double newScore);
}