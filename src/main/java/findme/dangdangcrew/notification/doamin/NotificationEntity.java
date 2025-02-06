package findme.dangdangcrew.notification.doamin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long targetUserId;
    private String placeId;
    private Long meetingId;
    private Long applyUserId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Builder
    public NotificationEntity(Long targetUserId,String placeId, Long meetingId,Long applyUserId, String message, boolean isRead, LocalDateTime createdAt, NotificationType notificationType){
        this.targetUserId =targetUserId;
        this.placeId = placeId;
        this.meetingId = meetingId;
        this.applyUserId = applyUserId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.notificationType = notificationType;
    }

    public void markAsRead(){
        this.isRead = true;
    }

}
