package findme.dangdangcrew.notification.doamin;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewMeetingNotification extends Notification{
    private Long meetingId;

    public NewMeetingNotification(Long targetUserId, Long meetingId){
        super(targetUserId, NotificationType.MEETING_CREATED);
        this.meetingId = meetingId;
    }
}
