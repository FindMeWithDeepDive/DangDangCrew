package findme.dangdangcrew.notification.converter;

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
                notificationEntity.getApplyUserId(),
                notificationEntity.getMeetingId(),
                notificationEntity.getCreatedAt());
    }

    @Override
    public boolean canConvertToEntity(Notification notification) {
        return notification.getNotificationType() == NotificationType.APPLY_MEETING;
    }

    @Override
    public NotificationEntity convertToEntity(Notification notification) {

        if (!(notification instanceof ApplyNotification applyNotification)) {
            throw new IllegalArgumentException("잘못된 알림 타입입니다. 기대한 타입: ApplyNotification, 받은 타입: "
                    + notification.getClass().getSimpleName());
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
