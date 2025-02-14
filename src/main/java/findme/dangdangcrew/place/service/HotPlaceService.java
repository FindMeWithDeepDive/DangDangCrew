package findme.dangdangcrew.place.service;

import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.global.service.RedisService;
import findme.dangdangcrew.notification.event.HotPlaceEvent;
import findme.dangdangcrew.place.domain.Place;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class HotPlaceService {

    // 20~30 참가 신청 수로 핫플레이스 기준 잡기/
    private static final int HOTPLACE_THRESHOLD = 3;
    private static final String HOTPLACE_PREFIX = "hotplace:";
    private final EventPublisher eventPublisher;
    private final PlaceService placeService;
    private final RedisService redisService;

    // fixedRate는 중복 실행의 위험이 있어, fixedDelay를 통해 작업이 끝남을 기준으로 처리
    @Scheduled(fixedDelay = 10000)
    public void checkHotPlaces(){
        // redis에서 hotplace: 로 시작하는 모든 키 조회
        log.info("[checkHotPlace] 핫 플레이스 크론 실행");
        Set<String> keys = redisService.scanHotPlaceKeys();
        log.info("keys 값 개수 : {}", keys.size());
        if(keys == null || keys.isEmpty()) return;

        for(String key : keys){
            String placeId = key.replace(HOTPLACE_PREFIX, "");
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
