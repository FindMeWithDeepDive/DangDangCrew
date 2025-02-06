package findme.dangdangcrew.notification.doamin;

import findme.dangdangcrew.global.config.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notifications")
public class NotificationEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long targetUserId;
    private String placeId;
    private Long meetingId;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

}
