package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.notification.domain.ApplyNotification;
import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import findme.dangdangcrew.notification.domain.NotificationType;

public class ApplyNotificationConverter implements NotificationConverter{

    @Override
    public boolean canConvert(NotificationEntity notificationEntity) {
        return notificationEntity.getNotificationType() == NotificationType.APPLY_MEETING;
    }

    @Override
    public Notification convert(NotificationEntity notificationEntity) {
        return new ApplyNotification(
                notificationEntity.getId(),
                notificationEntity.getTargetUserId(),
                notificationEntity.getMessage(),
                notificationEntity.isRead(),
                notificationEntity.getMeetingId(),
                notificationEntity.getApplyUserId(),
                notificationEntity.getCreatedAt());
    }

    @Override
    public boolean canConvertToEntity(Notification notification) {
        return notification.getNotificationType() == NotificationType.APPLY_MEETING;
    }

    @Override
    public NotificationEntity convertToEntity(Notification notification) {

        if (!(notification instanceof ApplyNotification applyNotification)) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_TYPE);
        }

        return NotificationEntity.builder()
                .targetUserId(applyNotification.getTargetUserId())
                .message(applyNotification.getMessage())
                .isRead(applyNotification.isRead())
                .createdAt(applyNotification.getCreatedAt())
                .meetingId(applyNotification.getMeetingId())
                .applyUserId(applyNotification.getApplyUserId())
                .notificationType(applyNotification.getNotificationType())
                .build();
    }
}
