package findme.dangdangcrew.sse.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
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
    public void deleteByEventId(String eventId) {
        emitters.remove(eventId);
    }

    @Override
    public void deleteAllEmittersByUserId(Long userId) {
        String userIdPrefix = userId +"_";
        emitters.forEach((k,v) -> {
            if(k.startsWith(userIdPrefix)) events.remove(k);
        });
    }

    @Override
    public Map<String, SseEmitter> findAllEmitters() {
        return emitters;
    }

    @Override
    public SseEmitter findEmitterByUserId(Long userId) {
        return emitters.entrySet().stream()
                .filter(e -> e.getKey().toString().startsWith(userId+"_"))  // userId로 시작하는 키 필터링
                .map(Map.Entry::getValue)  // Map.Entry에서 SseEmitter 값을 추출
                .findFirst()  // 첫 번째 결과를 찾음
                .orElse(null);  // 없을 경우 null 반환
    }

    @Override
    public Map<String, SseEmitter> findEmittersByUserId(Set<Long> userIds) {
        return emitters.entrySet().stream()
                .filter(entry -> {
                    String key = entry.getKey();
                    return userIds.stream().anyMatch(id -> key.startsWith(id + "_"));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
