package findme.dangdangcrew.notification.event;

import findme.dangdangcrew.notification.domain.*;
import findme.dangdangcrew.notification.repository.NotificationRepository;
import findme.dangdangcrew.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Transactional
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleNewMeetingNotification(NewMeetingEvent event) {
        // 보낼 메세지
        String message = "[" + event.placeName() + "]" + " 에 새로운 모임이 생성되었습니다";

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // MeetingNotification 생성
        Notification notification = new NewMeetingNotification(
                event.targetUserId(),
                message,
                false,
                event.meetingId(),
                createdAt
        );

        notificationService.saveNotification(notification);
    }

    @EventListener
    public void handleHotPlaceNotification(HotPlaceEvent event) {
        // 보낼 메세지
        String message = "[" + event.placeName() + "]" + " 인기 폭발! 많은 사람들이 관심을 가지고 있어요.";

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // MeetingNotification 생성
        Notification notification = new HotPlaceNotification(
                event.targetUserId(),
                message,
                false,
                event.placeId(),
                createdAt
        );

        notificationService.saveNotification(notification);
    }

    @EventListener
    @Transactional
    public void handleApplyNotification(ApplyEvent event) {
        // 보낼 메세지
        String message = event.nickname() + " 님께서 "+event.meetingName()+ " 에 참여 하고 싶어 합니다.";

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // MeetingNotification 생성
        Notification notification = new ApplyNotification(
                event.targetUserId(),
                message,
                false,
                event.meetingId(),
                event.applyUserId(),
                createdAt
        );

        notificationService.saveNotification(notification);
    }
}
