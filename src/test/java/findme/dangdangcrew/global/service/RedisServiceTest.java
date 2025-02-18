package findme.dangdangcrew.global.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    // Redis의 SET, GET, INCR, DEL 등의 명령을 수행하는 역할.
    private ValueOperations<String, String> valueOperations;
    @Mock
    private RedisConnectionFactory connectionFactory;
    @Mock
    private RedisConnection connection;
    @Mock
    private Cursor<byte[]> cursor;
    @InjectMocks
    private RedisService redisService;

    private static final String HOTPLACE_PREFIX = "hotplace:";
    private static final String EMAIL = "test@example.com";
    private static final String REFRESH_TOKEN = "sample_refresh_token";
    private static final String PLACE_ID = "12345";

    // lenient()는 Mockito에서 사용되지 않는 Stubbing으로 인해 발생하는 UnnecessaryStubbingException을 방지.
    @BeforeEach
    void setUp(){
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.getConnectionFactory()).thenReturn(connectionFactory);
        lenient().when(connectionFactory.getConnection()).thenReturn(connection);
    }
    @Test
    @DisplayName("리프레시 토큰을 저장합니다.")
    void saveRefreshTokenTest() {
        // when
        redisService.saveRefreshToken(EMAIL, REFRESH_TOKEN);

        // then
        verify(valueOperations, times(1)).set(eq("refresh:" + EMAIL), eq(REFRESH_TOKEN), eq(14L), eq(TimeUnit.DAYS));
    }

    @Test
    @DisplayName("리프레시 토큰을 조회합니다.")
    void getRefreshTokenTest() {
        // given
        when(valueOperations.get("refresh:" + EMAIL)).thenReturn(REFRESH_TOKEN);

        // when
        String token = redisService.getRefreshToken(EMAIL);

        // then
        assertEquals(REFRESH_TOKEN, token);
    }

    @Test
    @DisplayName("리프레시 토큰을 삭제합니다.")
    void deleteRefreshTokenTest() {

        // given
        when(redisTemplate.delete("refresh:" + EMAIL)).thenReturn(true);

        // when
        boolean result = redisService.deleteRefreshToken(EMAIL);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("장소 카운트를 증가시킵니다.")
    void incrementHotPlaceTest() {
        // given
        when(valueOperations.increment("hotplace:" + PLACE_ID)).thenReturn(1L);
        when(valueOperations.setIfAbsent(eq("ttl_lock:" + PLACE_ID), eq("1"), anyLong(), eq(TimeUnit.SECONDS)))
                .thenReturn(true);

        // when
        redisService.incrementHotPlace(PLACE_ID);

        // then
        verify(valueOperations, times(1)).increment("hotplace:" + PLACE_ID);
        verify(valueOperations, times(1)).setIfAbsent(eq("ttl_lock:" + PLACE_ID), eq("1"), anyLong(), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("핫 플레이스 관련 key 조회")
    void scanHotPlaceKeysTest() {
        // given
        byte[] key1 = (HOTPLACE_PREFIX + "123").getBytes();
        byte[] key2 = (HOTPLACE_PREFIX + "456").getBytes();

        List<byte[]> keys = Arrays.asList(key1, key2);
        Iterator<byte[]> iterators = keys.iterator();

        when(connection.scan(any(ScanOptions.class))).thenReturn(cursor);
        when(cursor.hasNext()).thenAnswer(invocation -> iterators.hasNext());
        when(cursor.next()).thenAnswer(invocation -> iterators.next());

        // when
        Set<String> resultKeys = redisService.scanHotPlaceKeys();

        // then
        assertNotNull(resultKeys);
        assertEquals(2, resultKeys.size());
        assertTrue(resultKeys.contains("hotplace:123"));
        assertTrue(resultKeys.contains("hotplace:456"));

        verify(connection, times(1)).scan(any(ScanOptions.class));
    }

    @Test
    @DisplayName("특정 장소의 참가자 수 조회")
    void getHotPlaceCountTest() {
        // given
        when(valueOperations.get("hotplace:" + PLACE_ID)).thenReturn("5");

        // when
        Long count = redisService.getHotPlaceCount(PLACE_ID);

        // then
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("이미 전송한 알림인지 체크합니다.")
    void isAlreadyNotifiedTest() {
        // given
        when(redisTemplate.hasKey("hotplace-notified:" + PLACE_ID)).thenReturn(true);

        // when
        boolean notified = redisService.isAlreadyNotified(PLACE_ID);

        // then
        assertTrue(notified);
    }

    @Test
    @DisplayName("전송 한 알림을 락 처리 합니다.")
    void markAsNotified() {
        // when
        redisService.markAsNotified(PLACE_ID);

        // then
        verify(valueOperations, times(1)).set(eq("hotplace-notified:" + PLACE_ID), eq("true"), anyLong(), eq(TimeUnit.SECONDS));

    }
}