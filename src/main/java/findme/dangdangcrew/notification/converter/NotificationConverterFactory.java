package findme.dangdangcrew.notification.converter;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
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
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_NOTIFICATION_TYPE))
                .convert(notificationEntity);
    }

    public NotificationEntity convertToEntity(Notification notification){
        return converters.stream()
                .filter(converters -> converters.canConvertToEntity(notification))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_NOTIFICATION_TYPE))
                .convertToEntity(notification);
    }

}
