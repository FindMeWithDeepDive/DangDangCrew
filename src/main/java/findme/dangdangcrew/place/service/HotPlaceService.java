package findme.dangdangcrew.place.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotPlaceService {

    private static final String HOTPLACE_PREFIX = "hotplace:";
    private static final String NOTIFIED_PREFIX = "hotplace-notified";
    private static final int HOTPLACE_THRESHOLD = 10;

    private final RedisTemplate<String, String> redisTemplate;




}
