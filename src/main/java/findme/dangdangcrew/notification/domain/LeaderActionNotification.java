package findme.dangdangcrew.notification.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LeaderActionNotification extends Notification{
    private Long meetingLeaderId;
    private Long meetingId;

    public LeaderActionNotification(Long targetUserId, String message, boolean isRead, Long meetingId, Long meetingLeaderId, LocalDateTime createdAt) {
        super(null, targetUserId, message, isRead, NotificationType.APPLY_MEETING, createdAt); // 저장 시 id 없이 생성
        this.meetingLeaderId = meetingLeaderId;
        this.meetingId = meetingId;
    }

    public LeaderActionNotification(Long id, Long targetUserId, String message, boolean isRead, Long meetingId, Long meetingLeaderId, LocalDateTime createdAt) {
        super(id, targetUserId, message, isRead, NotificationType.APPLY_MEETING, createdAt); // 조회 시 id 포함
        this.meetingLeaderId = meetingLeaderId;
        this.meetingId = meetingId;
    }
}
