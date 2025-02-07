package findme.dangdangcrew.notification.service;

import findme.dangdangcrew.notification.converter.NotificationConverterFactory;
import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import findme.dangdangcrew.notification.repository.NotificationRepository;
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

    // 특정 사용자의 모든 알림 조회
    public List<Notification> getUserNotifications(Long userId) {
        List<NotificationEntity> entities = notificationRepository.findByTargetUserId(userId);

        return entities.stream()
                .map(converterFactory::convert) // Entity → Domain 변환
                .collect(Collectors.toList());
    }

    // 특정 알림 조회
    @Transactional
    public Long readNotification(Long notificationId){
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));

        entity.markAsRead();
        return entity.getId();
    }

    // 알림 정보 저장
    @Transactional
    public void saveNotification(Notification notification){
        NotificationEntity notificationEntity = converterFactory.convertToEntity(notification);
        notificationRepository.save(notificationEntity);
    }
}
