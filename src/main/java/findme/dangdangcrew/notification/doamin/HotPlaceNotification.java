package findme.dangdangcrew.notification.doamin;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HotPlaceNotification extends Notification{
    private String placeId;

    public HotPlaceNotification(Long targetUserId, String placeId) {
        super(targetUserId, NotificationType.HOT_PLACE);
        this.placeId = placeId;
    }
}
