package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.notification.doamin.ApplyNotification;
import findme.dangdangcrew.notification.doamin.Notification;
import findme.dangdangcrew.notification.doamin.NotificationEntity;
import findme.dangdangcrew.notification.doamin.NotificationType;

public class ApplyNotificationConverter implements NotificationConverter{

    @Override
    public boolean canConvert(NotificationEntity notificationEntity) {
        return notificationEntity.getNotificationType() == NotificationType.APPLY_MEETING;
    }

    @Override
    public Notification convert(NotificationEntity notificationEntity) {
        return new ApplyNotification(
                notificationEntity.getTargetUserId(),
                notificationEntity.getMeetingId()
        );
    }
}
