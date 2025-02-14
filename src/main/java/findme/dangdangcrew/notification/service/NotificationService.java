package findme.dangdangcrew.notification.service;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.notification.converter.NotificationConverterFactory;
import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import findme.dangdangcrew.notification.repository.NotificationRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationConverterFactory converterFactory;
    private final UserService userService;

    // 특정 사용자의 모든 알림 조회
    public List<Notification> getUserNotifications() {
        User user = userService.getCurrentUser();
        List<NotificationEntity> entities = notificationRepository.findByTargetUserId(user.getId());

        boolean hasInvalidNotification = entities.stream()
                .anyMatch(notification -> !notification.getTargetUserId().equals(user.getId()));

        if(hasInvalidNotification){
            throw new CustomException(ErrorCode.NOTIFICATION_OWNER_MISMATCH);
        }

        return entities.stream()
                .map(converterFactory::convert) // Entity → Domain 변환
                .collect(Collectors.toList());
    }

    // 특정 알림 읽음 처리
    @Transactional
    public Long readNotification(Long notificationId){
        User user = userService.getCurrentUser();
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if(!user.getId().equals(entity.getTargetUserId())){
            throw new CustomException(ErrorCode.NOTIFICATION_OWNER_MISMATCH);
        }

        entity.markAsRead();
        return entity.getId();
    }

    // 알림 정보 저장
    @Transactional
    public void saveNotification(Notification notification){
        NotificationEntity notificationEntity = converterFactory.convertToEntity(notification);
        notificationRepository.save(notificationEntity);
    }

    @Transactional
    public void saveAllNotification(List<Notification> notifications) {
        List<NotificationEntity> notificationEntities = notifications.stream()
                .map(converterFactory::convertToEntity)
                .collect(Collectors.toList());
        notificationRepository.saveAll(notificationEntities);
    }
}
