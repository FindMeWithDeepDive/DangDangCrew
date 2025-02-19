package findme.dangdangcrew.notification.event;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.notification.domain.*;
import findme.dangdangcrew.notification.service.NotificationService;
import findme.dangdangcrew.place.domain.FavoritePlace;
import findme.dangdangcrew.place.repository.FavoritePlaceRepository;
import findme.dangdangcrew.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
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
@Log4j2
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final SseService sseService;

    @EventListener
    public void handleNewMeetingNotification(NewMeetingEvent event) {
        // 보낼 메세지
        String message = makeNewMeetingMessage(event);

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // 해당 장소를 즐겨찾기 한 유저 찾기
        List<FavoritePlace> favoritePlaces = favoritePlaceRepository.findAllByPlaceId(event.placeId());
        Set<Long> userIds = favoritePlaces.stream()
                .map(favoritePlace -> favoritePlace.getUser().getId())
                .collect(Collectors.toSet());

        if(favoritePlaces.isEmpty()){
            log.info("장소에 즐겨찾기를 한 유저가 존재하지 않습니다.");
            return;
        }

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
        String message = makeHotPlaceMessage(event);

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // 현재 연결되어있는 유저들 조회
        Set<Long> connectedUserIds = sseService.getConnectedUserIds();

        if(connectedUserIds.isEmpty()){
            log.info("현재 핫플레이스 알림을 받을 연결된 유저가 없습니다.");
            return;
        }

        // HotPlaceNotification 생성
        List<Notification> notifications = connectedUserIds.stream()
                .map(userId -> new HotPlaceNotification(
                        userId,
                        message,
                        false,
                        event.placeId(),
                        createdAt
                )).collect(Collectors.toList());

        notificationService.saveAllNotification(notifications);
        
        // 비동기 알림 보내기, 새로운 쓰레드에서는 트랜잭션이 전파되지 않음
        sseService.broadcastHotPlace(connectedUserIds, message);
    }

    @EventListener
    public void handleApplyNotification(ApplyEvent event) {
        // 보낼 메세지
        String message = makeApplyMessage(event);

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

    @EventListener
    public void handleLeaderActionNotification(LeaderActionEvent event) {
        // 보낼 메세지
        String message = makeLeaderActionMessage(event);

        // 생성 시각
        LocalDateTime createdAt = LocalDateTime.now();

        // MeetingNotification 생성
        Notification notification = new LeaderActionNotification(
                event.targetUserId(),
                message,
                false,
                event.meetingId(),
                createdAt
        );

        notificationService.saveNotification(notification);

        // 비동기 알림 보내기, 새로운 쓰레드에는 트랜잭션이 전파되지 않음
        sseService.sendNotificationToClient(event.targetUserId(), message);
    }

    private String makeNewMeetingMessage(NewMeetingEvent event) {
        return "[" + event.placeName() + "]" + " 에 새로운 모임이 생성되었습니다";
    }

    private String makeHotPlaceMessage(HotPlaceEvent event) {
        return "[" + event.placeName() + "]" + " 인기 폭발! 많은 사람들이 관심을 가지고 있어요.";
    }

    private String makeApplyMessage(ApplyEvent event) {
        return event.nickname() + " 님께서 " + event.meetingName() + " 에 참여 하고 싶어 합니다.";
    }
    private String makeLeaderActionMessage(LeaderActionEvent event) {
        LeaderActionType leaderActionType = event.leaderActionType();
        if(leaderActionType == LeaderActionType.JOIN_REJECTED)
            return "["+event.meetingName()+"]" + " 모임 참가 신청이 반려되었습니다.";

        if(leaderActionType == LeaderActionType.JOIN_ACCEPTED)
            return "["+event.meetingName()+"]" + " 모임 참가 신청이 승낙되었습니다.";

        if(leaderActionType == LeaderActionType.KICK_OUT)
            return "["+event.meetingName()+"]" + " 모임에서 강퇴되었습니다.";

        throw new CustomException(ErrorCode.INVALID_LEADER_ACTION_TYPE);
    }

}
