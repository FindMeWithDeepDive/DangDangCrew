package findme.dangdangcrew.notification.controller;

import findme.dangdangcrew.notification.doamin.Notification;
import findme.dangdangcrew.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 사용자의 모든 알림 조회 API
    // 토큰 기능 추가 이후 변경될 기능입니다.
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable("userId") Long userId){
        List<Notification> userNotifications = notificationService.getUserNotifications(userId);

        return ResponseEntity.ok(userNotifications);
    }

    // 특정 알림을 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<String> readNotification(@PathVariable("notificationId") Long notificationId) {
        Long readNotificationId = notificationService.readNotification(notificationId);
        return ResponseEntity.ok(readNotificationId + "번 알림이 읽음 처리되었습니다.");
    }
}
