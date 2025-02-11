package findme.dangdangcrew.sse.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class SseEmitterRepository implements EmitterRepository{
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> events = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String eventId, SseEmitter sseEmitter) {
        emitters.put(eventId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEvent(String eventId, Object event) {
        events.put(eventId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmittersStartWithUserId(Long userId) {
        return emitters.entrySet().stream()
                .filter(e -> e.getKey().startsWith(userId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventsStartWithUserId(Long userId) {
        return events.entrySet().stream()
                .filter(e->e.getKey().startsWith(userId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteByEventId(String eventId) {
        emitters.remove(eventId);
    }

    @Override
    public void deleteAllEmittersStartWithUserId(Long userId) {
        emitters.forEach((k,v) -> {
            if(k.startsWith(userId.toString())) events.remove(k);
        });
    }

    @Override
    public void deleteAllEventsStartWithUserId(Long userId) {
        events.forEach((k,v) ->{
            if(k.startsWith(userId.toString())) events.remove(k);
        });
    }

    @Override
    public Map<String, SseEmitter> findAllEmitters() {
        return emitters;
    }

    @Override
    public SseEmitter findEmitterByUserId(Long userId) {
        return emitters.entrySet().stream()
                .filter(e -> e.getKey().toString().startsWith(userId.toString()))  // userId로 시작하는 키 필터링
                .map(Map.Entry::getValue)  // Map.Entry에서 SseEmitter 값을 추출
                .findFirst()  // 첫 번째 결과를 찾음
                .orElse(null);  // 없을 경우 null 반환
    }
}
