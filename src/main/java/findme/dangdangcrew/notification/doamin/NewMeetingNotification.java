package findme.dangdangcrew.notification.doamin;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NewMeetingNotification extends Notification{
    private Long meetingId;

    public NewMeetingNotification(Long targetUserId,String message, boolean isRead, Long meetingId, LocalDateTime createdAt){
        super(targetUserId,message,isRead ,NotificationType.MEETING_CREATED, createdAt);
        this.meetingId = meetingId;
    }

    public static NotificationEntity toEntity(NewMeetingNotification notification) {
        return NotificationEntity.builder()
                .targetUserId(notification.targetUserId)
                .message(notification.message)
                .isRead(notification.isRead)
                .createdAt(notification.createdAt)
                .meetingId(notification.meetingId)
                .notificationType(notification.notificationType)
                .build();
    }
}
