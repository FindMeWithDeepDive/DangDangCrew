package findme.dangdangcrew.place.service;

import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.global.service.RedisService;
import findme.dangdangcrew.notification.event.HotPlaceEvent;
import findme.dangdangcrew.place.domain.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class HotPlaceService {
    private static final int HOTPLACE_THRESHOLD = 2;


    private final EventPublisher eventPublisher;
    private final PlaceService placeService;
    private final RedisService redisService;

    @Scheduled(fixedRate = 10000)
    public void checkHotPlaces(){
        // redis에서 hotplace: 로 시작하는 모든 키 조회
        Set<String> keys = redisService.getHotPlaceKeys();
        if(keys == null || keys.isEmpty()) return;

        for(String key : keys){
            String placeId = key.replace("hotplace:", "");
            Long count = redisService.getHotPlaceCount(placeId);

            if(count >= HOTPLACE_THRESHOLD && !redisService.isAlreadyNotified(placeId)){
                sendHotPlaceNotification(placeId);
                redisService.markAsNotified(placeId);
            }
        }
    }


    // 핫 플레이스 알림 전송
    private void sendHotPlaceNotification(String placeId){
        Place place = placeService.findPlaceByPlaceId(placeId);
        eventPublisher.publisher(new HotPlaceEvent(place.getPlaceName(),place.getId()));
    }

}
