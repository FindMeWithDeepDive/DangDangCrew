package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.notification.domain.HotPlaceNotification;
import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import findme.dangdangcrew.notification.domain.NotificationType;

public class HotPlaceNotificationConverter implements NotificationConverter {

    @Override
    public boolean canConvert(NotificationEntity notificationEntity) {
        return notificationEntity.getNotificationType() == NotificationType.HOT_PLACE;
    }

    @Override
    public Notification convert(NotificationEntity notificationEntity) {
        return new HotPlaceNotification(
                notificationEntity.getId(),
                notificationEntity.getTargetUserId(),
                notificationEntity.getMessage(),
                notificationEntity.isRead(),
                notificationEntity.getPlaceId(),
                notificationEntity.getCreatedAt());
    }

    @Override
    public boolean canConvertToEntity(Notification notification) {
        return notification.getNotificationType() == NotificationType.HOT_PLACE;
    }

    @Override
    public NotificationEntity convertToEntity(Notification notification) {
        if (!(notification instanceof HotPlaceNotification hotPlaceNotification)) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_TYPE);
        }

        return NotificationEntity.builder()
                .targetUserId(hotPlaceNotification.getTargetUserId())
                .message(hotPlaceNotification.getMessage())
                .isRead(hotPlaceNotification.isRead())
                .createdAt(hotPlaceNotification.getCreatedAt())
                .placeId(hotPlaceNotification.getPlaceId())
                .notificationType(hotPlaceNotification.getNotificationType())
                .build();
    }

}
