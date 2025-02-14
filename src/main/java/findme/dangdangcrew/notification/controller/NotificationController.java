package findme.dangdangcrew.notification.controller;

import findme.dangdangcrew.global.dto.ResponseDto;
import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "[Notification] Notification API", description = "알림을 조회하고 읽음 처리합니다.")
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 사용자의 모든 알림 조회 API
    @Operation(summary = "유저의 알림 조회",description = "헤더의 토큰 기반으로 유저의 알림들을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto<List<Notification>>> getUserNotifications(@PathVariable("userId") Long userId){
        List<Notification> userNotifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ResponseDto.of(
                userNotifications,
                "유저의 알림을 성공적으로 조회하였습니다."));
    }

    // 특정 알림을 읽음 처리
    @Operation(summary = "유저의 알림 읽음 처리",description = "헤더의 토큰 기반으로 유저의 알림들을 읽음 처리합니다.")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ResponseDto> readNotification(@PathVariable("notificationId") Long notificationId) {
        Long readNotificationId = notificationService.readNotification(notificationId);
        String message = readNotificationId + "번 알림이 읽음 처리되었습니다.";
        return ResponseEntity.ok(ResponseDto.of(null, message));
    }
}
