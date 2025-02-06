package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.notification.doamin.NewMeetingNotification;
import findme.dangdangcrew.notification.doamin.Notification;
import findme.dangdangcrew.notification.doamin.NotificationEntity;
import findme.dangdangcrew.notification.doamin.NotificationType;

public class NewMeetingNotificationConverter implements NotificationConverter{

    @Override
    public boolean canConvert(NotificationEntity notificationEntity) {
        return notificationEntity.getNotificationType() == NotificationType.MEETING_CREATED;
    }

    @Override
    public Notification convert(NotificationEntity notificationEntity) {
        return new NewMeetingNotification(
                notificationEntity.getTargetUserId(),
                notificationEntity.getMessage(),
                notificationEntity.isRead(),
                notificationEntity.getMeetingId(),
                notificationEntity.getCreatedAt());
    }
}
