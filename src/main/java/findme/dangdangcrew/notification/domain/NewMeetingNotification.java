package findme.dangdangcrew.notification.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NewMeetingNotification extends Notification{
    private Long meetingId;
    private String placeId;

    public NewMeetingNotification(Long targetUserId, String message, boolean isRead, Long meetingId, String placeId, LocalDateTime createdAt){
        super(null, targetUserId, message, isRead , NotificationType.MEETING_CREATED, createdAt);
        this.meetingId = meetingId;
        this.placeId = placeId;
    }

    public NewMeetingNotification(Long id,Long targetUserId,String message, boolean isRead, Long meetingId, String placeId, LocalDateTime createdAt){
        super(id, targetUserId,message,isRead ,NotificationType.MEETING_CREATED, createdAt);
        this.meetingId = meetingId;
        this.placeId = placeId;
    }
}
