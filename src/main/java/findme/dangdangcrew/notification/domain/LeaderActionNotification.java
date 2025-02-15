package findme.dangdangcrew.notification.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LeaderActionNotification extends Notification{
    private Long meetingId;

    public LeaderActionNotification(Long targetUserId, String message, boolean isRead, Long meetingId, LocalDateTime createdAt) {
        super(null, targetUserId, message, isRead, NotificationType.LEADER_ACTION, createdAt); // 저장 시 id 없이 생성
        this.meetingId = meetingId;
    }

    public LeaderActionNotification(Long id, Long targetUserId, String message, boolean isRead, Long meetingId, LocalDateTime createdAt) {
        super(id, targetUserId, message, isRead, NotificationType.LEADER_ACTION, createdAt); // 조회 시 id 포함
        this.meetingId = meetingId;
    }
}
