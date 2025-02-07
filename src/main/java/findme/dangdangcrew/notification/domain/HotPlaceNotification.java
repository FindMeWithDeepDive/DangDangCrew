package findme.dangdangcrew.notification.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class HotPlaceNotification extends Notification{
    private String placeId;


    public HotPlaceNotification(Long targetUserId, String message, boolean isRead, String placeId, LocalDateTime createdAt) {
        super(null, targetUserId,message, isRead, NotificationType.HOT_PLACE,createdAt);
        this.placeId = placeId;
    }
    public HotPlaceNotification(Long id, Long targetUserId, String message, boolean isRead, String placeId, LocalDateTime createdAt) {
        super(id, targetUserId,message, isRead, NotificationType.HOT_PLACE,createdAt);
        this.placeId = placeId;
    }
}
