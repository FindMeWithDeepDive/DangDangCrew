package findme.dangdangcrew.notification.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ApplyNotification extends Notification {
    private Long applyUserId;
    private Long meetingId;

    public ApplyNotification(Long targetUserId, String message, boolean isRead, Long meetingId, Long applyUserId, LocalDateTime createdAt) {
        super(null, targetUserId, message, isRead, NotificationType.APPLY_MEETING, createdAt); // ✅ 저장 시 id 없이 생성
        this.applyUserId = applyUserId;
        this.meetingId = meetingId;
    }

    public ApplyNotification(Long id, Long targetUserId, String message, boolean isRead, Long meetingId, Long applyUserId, LocalDateTime createdAt) {
        super(id, targetUserId, message, isRead, NotificationType.APPLY_MEETING, createdAt); // ✅ 조회 시 id 포함
        this.applyUserId = applyUserId;
        this.meetingId = meetingId;
    }
}

