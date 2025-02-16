package findme.dangdangcrew.place.service;

import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.global.service.RedisService;
import findme.dangdangcrew.notification.event.HotPlaceEvent;
import findme.dangdangcrew.place.domain.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotPlaceServiceTest {
    @InjectMocks
    private HotPlaceService hotPlaceService;
    @Mock
    private RedisService redisService;
    @Mock
    private PlaceService placeService;
    @Mock
    private EventPublisher eventPublisher;

    private static final String HOTPLACE_PREFIX = "hotplace:";
    private static final int HOTPLACE_THRESHOLD = 2;


    @Test
    @DisplayName("핫 플레이스 체크 - 참가 신청자 수 기준 충족 시 알림 전송")
    void checkHotPlacesTest_Send_Notification() {
        // given
        Set<String> hotPlaceKeys = new HashSet<>();
        hotPlaceKeys.add(HOTPLACE_PREFIX + "place123");
        hotPlaceKeys.add(HOTPLACE_PREFIX + "place456");

        when(redisService.scanHotPlaceKeys()).thenReturn(hotPlaceKeys);
        when(redisService.getHotPlaceCount("place123")).thenReturn((long) HOTPLACE_THRESHOLD);
        when(redisService.getHotPlaceCount("place456")).thenReturn((long) HOTPLACE_THRESHOLD + 1);

        when(redisService.isAlreadyNotified("place123")).thenReturn(false);
        when(redisService.isAlreadyNotified("place456")).thenReturn(false);

        Place testPlace1 = Place.builder().id("place123").placeName("testPlace1").x(12.345).y(12.345).build();
        Place testPlace2 = Place.builder().id("place456").placeName("testPlace2").x(54.321).y(54.321).build();

        when(placeService.findPlaceByPlaceId("place123")).thenReturn(testPlace1);
        when(placeService.findPlaceByPlaceId("place456")).thenReturn(testPlace2);

        // when
        hotPlaceService.checkHotPlaces();

        // then
        verify(redisService, times(2)).getHotPlaceCount(anyString());
        verify(redisService, times(2)).isAlreadyNotified(anyString());
        verify(redisService, times(2)).markAsNotified(anyString());
        verify(placeService, times(2)).findPlaceByPlaceId(anyString());
        verify(eventPublisher, times(2)).publisher(any(HotPlaceEvent.class));
    }

    @Test
    @DisplayName("핫 플레이스 체크 - 중복된 알림은 전송하지 않습니다.")
    void checkHotPlacesTest_Duplication_Notification() {
        // given
        Set<String> hotPlaceKeys = new HashSet<>();
        hotPlaceKeys.add(HOTPLACE_PREFIX + "place123");

        when(redisService.scanHotPlaceKeys()).thenReturn(hotPlaceKeys);
        when(redisService.getHotPlaceCount("place123")).thenReturn((long) HOTPLACE_THRESHOLD);
        when(redisService.isAlreadyNotified("place123")).thenReturn(true);

        // when
        hotPlaceService.checkHotPlaces();

        // then
        verify(redisService, times(1)).getHotPlaceCount(anyString());
        verify(redisService, times(1)).isAlreadyNotified(anyString());
        verify(redisService, never()).markAsNotified(anyString());
        verify(placeService, never()).findPlaceByPlaceId(anyString());
        verify(eventPublisher, never()).publisher(any(HotPlaceEvent.class));
    }

}