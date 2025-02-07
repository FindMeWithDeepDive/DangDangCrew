package findme.dangdangcrew.notification.converter;

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
                notificationEntity.getCreatedAt());
    }

    @Override
    public boolean canConvertToEntity(Notification notification) {
        return notification.getNotificationType() == NotificationType.MEETING_CREATED;
    }

    @Override
    public NotificationEntity convertToEntity(Notification notification) {
        if (!(notification instanceof NewMeetingNotification newMeetingNotification)) {
            throw new IllegalArgumentException("잘못된 알림 타입입니다. 기대한 타입: NewMeetingNotification, 받은 타입: "
                    + notification.getClass().getSimpleName());
        }

        return NotificationEntity.builder()
                .targetUserId(newMeetingNotification.getTargetUserId())
                .message(newMeetingNotification.getMessage())
                .isRead(newMeetingNotification.isRead())
                .createdAt(newMeetingNotification.getCreatedAt())
                .meetingId(newMeetingNotification.getMeetingId()) // 🔹 모임 ID 포함
                .notificationType(newMeetingNotification.getNotificationType()) // 🔹 알림 타입 설정
                .build();
    }

}
