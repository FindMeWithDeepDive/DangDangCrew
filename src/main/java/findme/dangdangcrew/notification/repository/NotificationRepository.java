package findme.dangdangcrew.notification.repository;

import findme.dangdangcrew.notification.domain.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findByTargetUserId(Long targetUserId, Pageable pageable);
}
