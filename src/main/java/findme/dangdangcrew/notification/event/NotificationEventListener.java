package findme.dangdangcrew.notification.event;

import findme.dangdangcrew.notification.domain.ApplyNotification;
import findme.dangdangcrew.notification.domain.HotPlaceNotification;
import findme.dangdangcrew.notification.domain.NewMeetingNotification;
import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.service.NotificationService;
import findme.dangdangcrew.place.domain.FavoritePlace;
import findme.dangdangcrew.place.repository.FavoritePlaceRepository;
import findme.dangdangcrew.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final SseService sseService;

    @EventListener
    public void handleNewMeetingNotification(NewMeetingEvent event) {
        // 보낼 메세지
        String message = "[" + event.placeName() + "]" + " 에 새로운 모임이 생성되었습니다";

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // 미팅이 생성된 장소 정보 조회 -> 해당 장소를 즐겨찾기 한 유저 찾기
        List<FavoritePlace> favoritePlaces = favoritePlaceRepository.findAllByPlaceId(event.placeId());
        Set<Long> userIds = favoritePlaces.stream()
                .map(favoritePlace -> favoritePlace.getUser().getId())
                .collect(Collectors.toSet());

        // MeetingNotification 생성
        List<Notification> notifications = userIds.stream()
                        .map(userId-> new NewMeetingNotification(
                                userId,
                                message,
                                false,
                                event.meetingId(),
                                event.placeId(),
                                createdAt
                        )).collect(Collectors.toList());

        notificationService.saveAllNotification(notifications);

        // 비동기 알림 보내기, 새로운 쓰레드에서는 트랜잭션이 전파되지 않음
        sseService.broadcastNewMeeting(userIds, message);

    }

    @EventListener
    public void handleHotPlaceNotification(HotPlaceEvent event) {
        // 보낼 메세지
        String message = "[" + event.placeName() + "]" + " 인기 폭발! 많은 사람들이 관심을 가지고 있어요.";

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // 현재 연결되어있는 유저들 조회
        Set<Long> connectUserId = sseService.getConnectedUserIds();

        // HotPlaceNotification 생성
        List<Notification> notifications = connectUserId.stream()
                .map(userId -> new HotPlaceNotification(
                        userId,
                        message,
                        false,
                        event.placeId(),
                        createdAt
                )).collect(Collectors.toList());

        notificationService.saveAllNotification(notifications);
        
        // 비동기 알림 보내기, 새로운 쓰레드에서는 트랜잭션이 전파되지 않음
        sseService.broadcastHotPlace(connectUserId, message);
    }

    @EventListener
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

        // 비동기 알림 보내기, 새로운 쓰레드에는 트랜잭션이 전파되지 않음
        sseService.sendNotificationToClient(event.targetUserId(), message);
    }
}
