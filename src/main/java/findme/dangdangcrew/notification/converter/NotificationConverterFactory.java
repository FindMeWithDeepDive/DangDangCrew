package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationConverterFactory {
    private final List<NotificationConverter> converters;

    public NotificationConverterFactory(){
        this.converters = List.of(
                new ApplyNotificationConverter(),
                new NewMeetingNotificationConverter(),
                new HotPlaceNotificationConverter()
        );
    }

    public Notification convert(NotificationEntity notificationEntity) {
        return converters.stream()
                .filter(converter -> converter.canConvert(notificationEntity))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 알림 타입입니다."))
                .convert(notificationEntity);
    }

    public NotificationEntity convertToEntity(Notification notification){
        return converters.stream()
                .filter(converters -> converters.canConvertToEntity(notification))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 알림 타입입니다."))
                .convertToEntity(notification);
    }

}
