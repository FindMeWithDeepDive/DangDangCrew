package findme.dangdangcrew.notification.doamin;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public abstract class Notification {
    protected Long id;
    protected Long targetUserId;
    protected String message;
    protected boolean isRead;
    protected LocalDateTime createdAt;
    protected NotificationType notificationType;

    public Notification(
            Long targetUserId,
            String message,
            boolean isRead,
            NotificationType notificationType,
            LocalDateTime createdAt) {
        this.targetUserId = targetUserId;
        this.notificationType = notificationType;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }
}
