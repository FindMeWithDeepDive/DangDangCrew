package findme.dangdangcrew.sse.service;

import findme.dangdangcrew.sse.infrastructure.ClockHolder;
import findme.dangdangcrew.sse.repository.EmitterRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SseServiceTest {

    @Mock
    private EmitterRepository emitterRepository;

    @Mock
    private ClockHolder clockHolder;

    @Mock
    private UserService userService;

    @InjectMocks
    private SseService sseService;

    @Test
    @DisplayName("연결된 유저 ID 리스트를 반환합니다.")
    void getConnectedUserIdsTest() {
        // given
        Map<String, SseEmitter> emitters = new HashMap<>();
        emitters.put("1_12345", new SseEmitter());
        emitters.put("2_12345", new SseEmitter());

        when(emitterRepository.findAllEmitters()).thenReturn(emitters);

        // when
        Set<Long> connectedUserIds = sseService.getConnectedUserIds();

        // then
        assertEquals(2, connectedUserIds.size());
        assertTrue(connectedUserIds.contains(1L));
        assertTrue(connectedUserIds.contains(2L));
    }

    @Test
    @DisplayName("SSE 구독 시 SseEmitter 를 생성 후 저장합니다.")
    void subscribeTest() {
        // given
        Long userId = 1L;
        User user = mock(User.class);
        String eventId = userId + "_12345";
        SseEmitter testEmitter = new SseEmitter(60*1000L);

        when(clockHolder.mills()).thenReturn(12345L);
        when(emitterRepository.save(eq(eventId), any(SseEmitter.class))).thenReturn(testEmitter);
        when(userService.getCurrentUser()).thenReturn(user);
        given(user.getId()).willReturn(userId);

        // when
        SseEmitter result = sseService.subscribe();

        // then
        assertNotNull(result);
        verify(emitterRepository, times(1)).save(eq(eventId), any(SseEmitter.class));
    }

//    @Test
//    @DisplayName("실시간 핫플레이스 알림을 연결된 유저들에게 전송해야 한다.")
//    void broadcastHotPlace() throws IOException {
//        // given
//        Set<Long> userIds = Set.of(1L, 2L);
//        String message = "핫 플레이스 알림 테스트";
//
//        SseEmitter emitter1 = mock(SseEmitter.class);
//        SseEmitter emitter2 = mock(SseEmitter.class);
//
//        when(emitterRepository.findEmitterByUserId(1L)).thenReturn(emitter1);
//        when(emitterRepository.findEmitterByUserId(2L)).thenReturn(emitter2);
//
//        // when
//        sseService.broadcastHotPlace(userIds, message);
//
//        // then
//        verify(emitter1, times(1)).send(any(SseEmitter.SseEventBuilder.class));
//        verify(emitter2, times(1)).send(any(SseEmitter.SseEventBuilder.class));
//    }

//    @Test
//    @DisplayName("즐겨찾기 한 유저들에게 새로운 모임 알림을 전송합니다.")
//    void broadcastNewMeeting() throws IOException {
//        // given
//        Set<Long> userIds = Set.of(1L, 2L, 3L);
//        String message = "새로운 모임이 생겼습니다.";
//
//        // Mock SseEmitter
//        SseEmitter mockEmitter = mock(SseEmitter.class);
//        when(emitterRepository.findEmittersByUserId(userIds)).thenReturn(
//                Map.of("1_123456", mockEmitter, "2_123457", mockEmitter, "3_123458", mockEmitter)
//        );
//
//        // ArgumentCaptor 설정
//        ArgumentCaptor<SseEmitter.SseEventBuilder> eventCaptor = ArgumentCaptor.forClass(SseEmitter.SseEventBuilder.class);
//
//        // when
//        sseService.broadcastNewMeeting(userIds, message);
//
//        // then
//        verify(mockEmitter, times(3)).send(eventCaptor.capture());
//
//        // 캡처된 이벤트 검증
//        SseEmitter.SseEventBuilder capturedEvent = eventCaptor.getValue();
//        assertNotNull(capturedEvent);
//    }

    @Test
    @DisplayName("유저에게 개별 알림을 전송합니다.")
    void sendNotificationToClient() throws IOException {
        // given
        Long userId = 1L;
        String message = "개발 알림 전송";

        SseEmitter emitter = mock(SseEmitter.class);
        when(emitterRepository.findEmitterByUserId(userId)).thenReturn(emitter);

        // ArgumentCaptor 세팅
        ArgumentCaptor<SseEmitter.SseEventBuilder> eventCaptor = ArgumentCaptor.forClass(SseEmitter.SseEventBuilder.class);

        // when
        sseService.sendNotificationToClient(userId, message);

        // then
        verify(emitter, times(1)).send(eventCaptor.capture());

        SseEmitter.SseEventBuilder capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
    }
}