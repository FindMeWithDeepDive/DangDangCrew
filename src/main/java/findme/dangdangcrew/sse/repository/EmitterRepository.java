package findme.dangdangcrew.sse.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EmitterRepository {
    SseEmitter save(String eventId, SseEmitter sseEmitter);
    void saveEvent(String eventId, Object event);
    Map<String, SseEmitter> findAllEmittersStartWithUserId(Long userId);
    Map<String, Object> findAllEventsStartWithUserId(Long userId);
    void deleteByEventId(String eventId);
    void deleteAllEmittersStartWithUserId(Long userId);
    void deleteAllEventsStartWithUserId(Long userId);
    Map<String, SseEmitter> findAllEmitters();

    SseEmitter findEmitterByUserId(Long userId);
    Map<String, SseEmitter> findEmittersByUserId(Set<Long> userIds);
}
