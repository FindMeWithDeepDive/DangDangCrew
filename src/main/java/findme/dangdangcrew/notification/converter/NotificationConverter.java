package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.notification.doamin.Notification;
import findme.dangdangcrew.notification.doamin.NotificationEntity;

public interface NotificationConverter {

    boolean canConvert(NotificationEntity notificationEntity);
    Notification convert(NotificationEntity notificationEntity);
}
