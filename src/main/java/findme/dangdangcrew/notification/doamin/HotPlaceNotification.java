package findme.dangdangcrew.notification.doamin;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HotPlaceNotification extends Notification{
    private Long placeId;

    public HotPlaceNotification(Long targetUserId, Long placeId) {
        super(targetUserId, NotificationType.HOT_PLACE);
        this.placeId = placeId;
    }
}
