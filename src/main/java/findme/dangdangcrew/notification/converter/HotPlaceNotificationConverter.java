package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.notification.doamin.HotPlaceNotification;
import findme.dangdangcrew.notification.doamin.Notification;
import findme.dangdangcrew.notification.doamin.NotificationEntity;
import findme.dangdangcrew.notification.doamin.NotificationType;

public class HotPlaceNotificationConverter implements NotificationConverter {

    @Override
    public boolean canConvert(NotificationEntity notificationEntity) {
        return notificationEntity.getNotificationType() == NotificationType.HOT_PLACE;
    }

    @Override
    public Notification convert(NotificationEntity notificationEntity) {
        return new HotPlaceNotification(
                notificationEntity.getTargetUserId(),
                notificationEntity.getMessage(),
                notificationEntity.isRead(),
                notificationEntity.getPlaceId(),
                notificationEntity.getCreatedAt());
    }
}
