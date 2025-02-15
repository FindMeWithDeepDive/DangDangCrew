package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.notification.domain.NewMeetingNotification;
import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import findme.dangdangcrew.notification.domain.NotificationType;

public class NewMeetingNotificationConverter implements NotificationConverter{

    @Override
    public boolean canConvert(NotificationEntity notificationEntity) {
        return notificationEntity.getNotificationType() == NotificationType.MEETING_CREATED;
    }

    @Override
    public Notification convert(NotificationEntity notificationEntity) {
        return new NewMeetingNotification(
                notificationEntity.getId(),
                notificationEntity.getTargetUserId(),
                notificationEntity.getMessage(),
                notificationEntity.isRead(),
                notificationEntity.getMeetingId(),
                notificationEntity.getPlaceId(),
                notificationEntity.getCreatedAt());
    }

    @Override
    public boolean canConvertToEntity(Notification notification) {
        return notification.getNotificationType() == NotificationType.MEETING_CREATED;
    }

    @Override
    public NotificationEntity convertToEntity(Notification notification) {
        if (!(notification instanceof NewMeetingNotification newMeetingNotification)) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_TYPE);
        }

        return NotificationEntity.builder()
                .targetUserId(newMeetingNotification.getTargetUserId())
                .message(newMeetingNotification.getMessage())
                .isRead(newMeetingNotification.isRead())
                .createdAt(newMeetingNotification.getCreatedAt())
                .meetingId(newMeetingNotification.getMeetingId())
                .notificationType(newMeetingNotification.getNotificationType())
                .build();
    }

}
