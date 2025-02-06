package findme.dangdangcrew.notification.doamin;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public abstract class Notification {
    protected Long id;
    protected Long targetUserId;
    protected LocalDateTime createdAt;
    protected NotificationType notificationType;

    public Notification(Long targetUserId, NotificationType notificationType) {
        this.targetUserId = targetUserId;
        this.notificationType = notificationType;
        this.createdAt = LocalDateTime.now();
    }
}
