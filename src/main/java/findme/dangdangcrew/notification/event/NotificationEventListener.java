package findme.dangdangcrew.notification.event;

import findme.dangdangcrew.notification.doamin.ApplyNotification;
import findme.dangdangcrew.notification.doamin.HotPlaceNotification;
import findme.dangdangcrew.notification.doamin.NewMeetingNotification;
import findme.dangdangcrew.notification.doamin.NotificationEntity;
import findme.dangdangcrew.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Transactional
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleNewMeetingNotification(NewMeetingEvent event) {
        // 보낼 메세지
        String message = "[" + event.placeName() + "]" + " 에 새로운 모임이 생성되었습니다";

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // MeetingNotification 생성
        NewMeetingNotification notification = new NewMeetingNotification(
                event.targetUserId(),
                message,
                false,
                event.meetingId(),
                createdAt
        );

        // JPA 엔티티로 변환 후 저장
        NotificationEntity entity = NewMeetingNotification.toEntity(notification);

        notificationRepository.save(entity);
    }

    @EventListener
    public void handleHotPlaceNotification(HotPlaceEvent event) {
        // 보낼 메세지
        String message = "[" + event.placeName() + "]" + " 인기 폭발! 많은 사람들이 관심을 가지고 있어요.";

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // MeetingNotification 생성
        HotPlaceNotification notification = new HotPlaceNotification(
                event.targetUserId(),
                message,
                false,
                event.placeId(),
                createdAt
        );

        // JPA 엔티티로 변환 후 저장
        NotificationEntity entity = HotPlaceNotification.toEntity(notification);

        notificationRepository.save(entity);
    }

    @EventListener
    @Transactional
    public void handleApplyNotification(ApplyEvent event) {
        // 보낼 메세지
        String message = event.nickname() + " 님께서 "+event.meetingName()+ " 에 참여 하고 싶어 합니다.";

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // MeetingNotification 생성
        ApplyNotification notification = new ApplyNotification(
                event.targetUserId(),
                message,
                false,
                event.meetingId(),
                event.applyUserId(),
                createdAt
        );

        // JPA 엔티티로 변환 후 저장
        NotificationEntity entity = ApplyNotification.toEntity(notification);

        notificationRepository.save(entity);
    }
}
