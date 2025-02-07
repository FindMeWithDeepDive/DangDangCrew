package findme.dangdangcrew.notification.repository;

import findme.dangdangcrew.notification.domain.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByTargetUserId(Long targetUserId);
}
