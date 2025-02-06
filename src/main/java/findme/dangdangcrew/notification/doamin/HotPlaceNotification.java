package findme.dangdangcrew.notification.doamin;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class HotPlaceNotification extends Notification{
    private String placeId;

    public HotPlaceNotification(Long targetUserId, String message, boolean isRead, String placeId, LocalDateTime createdAt) {
        super(targetUserId,message, isRead, NotificationType.HOT_PLACE,createdAt);
        this.placeId = placeId;
    }

    public static NotificationEntity toEntity(HotPlaceNotification notification) {
        return NotificationEntity.builder()
                .targetUserId(notification.targetUserId)
                .message(notification.message)
                .isRead(notification.isRead)
                .createdAt(notification.createdAt)
                .placeId(notification.placeId)
                .notificationType(notification.notificationType)
                .build();
    }
}
