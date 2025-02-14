package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.notification.domain.*;

public class LeaderActionNotificationConverter implements NotificationConverter {

    @Override
    public boolean canConvert(NotificationEntity notificationEntity) {
        return notificationEntity.getNotificationType() == NotificationType.LEADER_ACTION;
    }

    @Override
    public Notification convert(NotificationEntity notificationEntity) {
        return new LeaderActionNotification(
                notificationEntity.getId(),
                notificationEntity.getTargetUserId(),
                notificationEntity.getMessage(),
                notificationEntity.isRead(),
                notificationEntity.getMeetingId(),
                notificationEntity.getMeetingLeaderId(),
                notificationEntity.getCreatedAt());
    }

    @Override
    public boolean canConvertToEntity(Notification notification) {
        return notification.getNotificationType() == NotificationType.LEADER_ACTION;
    }

    @Override
    public NotificationEntity convertToEntity(Notification notification) {
        if (!(notification instanceof LeaderActionNotification leaderActionNotification)) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_TYPE);
        }
        return NotificationEntity.builder()
                .targetUserId(leaderActionNotification.getTargetUserId())
                .message(leaderActionNotification.getMessage())
                .isRead(leaderActionNotification.isRead())
                .createdAt(leaderActionNotification.getCreatedAt())
                .meetingId(leaderActionNotification.getMeetingId())
                .applyUserId(leaderActionNotification.getMeetingLeaderId())
                .notificationType(leaderActionNotification.getNotificationType())
                .build();
    }
}
