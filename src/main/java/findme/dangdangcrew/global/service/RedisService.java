package findme.dangdangcrew.global.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final long EXPIRATION_DAYS = 14;

    private static final String HOTPLACE_PREFIX = "hotplace:";
    private static final String  NOTIFIED_PREFIX = "hotplace-notified:";
    private static final int EXPIRATION_TIME = 24 * 60 * 60; // 24시간 (알림 중복 방지)


    // RefreshToken 저장
    public void saveRefreshToken(String email, String refreshToken){
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, refreshToken,EXPIRATION_DAYS, TimeUnit.DAYS);
    }

    // RefreshToken 조회
    public String getRefreshToken(String email){
        return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + email);
    }

    // RefreshToken 삭제
    public boolean deleteRefreshToken(String email){
        return Boolean.TRUE.equals(redisTemplate.delete(REFRESH_TOKEN_PREFIX+email));
    }


    // 장소별 참가자 수 증가
    public void incrementHotPlace(String placeId) {
        String key = HOTPLACE_PREFIX + placeId;
        Long count = redisTemplate.opsForValue().increment(key);

        if(count == 1){
            redisTemplate.expire(key,EXPIRATION_TIME, TimeUnit.SECONDS);
        }
    }

    // 핫 플레이스 관련 key 조회
    public Set<String> getHotPlaceKeys(){
        return redisTemplate.keys(HOTPLACE_PREFIX+"*");
    }

    // 특정 장소의 참가자 수 조회
    public Long getHotPlaceCount(String placeId){
        String countStr = redisTemplate.opsForValue().get(HOTPLACE_PREFIX+placeId);
        return(countStr !=null) ? Long.valueOf(countStr) : 0;
    }

    // 해당 장소가 이미 알림이 발송되었는지 확인
    public boolean isAlreadyNotified(String placeId){
        return Boolean.TRUE.equals(redisTemplate.hasKey(NOTIFIED_PREFIX+placeId));
    }

    // 핫 플레이스 알림을 하루 동안 중복 발송되지 않도록 설정
    public void markAsNotified(String placeId){
        redisTemplate.opsForValue().set(NOTIFIED_PREFIX + placeId, "true", EXPIRATION_TIME, TimeUnit.SECONDS);
    }
}
