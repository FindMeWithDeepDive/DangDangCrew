package findme.dangdangcrew.notification.doamin;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ApplyNotification extends Notification {
    private Long applyUserId;
    private Long meetingId;

    public ApplyNotification(Long targetUserId, String message , boolean isRead,Long meetingId, Long applyUserId, LocalDateTime createdAt){
        super(targetUserId,message,isRead, NotificationType.APPLY_MEETING, createdAt);
        this.applyUserId = applyUserId;
        this.meetingId = meetingId;
    }

    public static NotificationEntity toEntity(ApplyNotification notification) {
        return NotificationEntity.builder()
                .targetUserId(notification.targetUserId)
                .message(notification.message)
                .isRead(notification.isRead)
                .createdAt(notification.createdAt)
                .meetingId(notification.meetingId)
                .applyUserId(notification.applyUserId)
                .notificationType(notification.notificationType)
                .build();
    }
}

