package findme.dangdangcrew.notification.doamin;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplyNotification extends Notification {
    private Long applyUserId;

    public ApplyNotification(Long targetUserId, Long applyUserId){
        super(targetUserId, NotificationType.APPLY_MEETING);
        this.applyUserId = applyUserId;
    }
}
