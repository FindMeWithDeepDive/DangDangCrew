package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import org.aspectj.weaver.ast.Not;

public interface NotificationConverter {

    boolean canConvert(NotificationEntity notificationEntity);
    Notification convert(NotificationEntity notificationEntity);
    boolean canConvertToEntity(Notification notification);
    NotificationEntity convertToEntity(Notification notification);
}
