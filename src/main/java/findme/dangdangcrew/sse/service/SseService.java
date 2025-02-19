package findme.dangdangcrew.sse.service;

import findme.dangdangcrew.sse.infrastructure.ClockHolder;
import findme.dangdangcrew.sse.repository.EmitterRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class SseService {
    private final EmitterRepository emitterRepository;
    private final ClockHolder clockHolder;
    private final UserService userService;

    @Qualifier("customTaskExecutor")
    private final ThreadPoolTaskExecutor customTaskExecutor;
    private static final long TIMEOUT = 1800*1000L;
    private static final long RECONNECTION_TIMEOUT = 1000L;

    // 알림을 저장하기 위한 연결된 userId 가져오는 로직
    public Set<Long> getConnectedUserIds(){
        return emitterRepository.findAllEmitters().keySet().stream()
                .map(k -> Long.parseLong(k.split("_")[0]))
                .collect(Collectors.toSet());
    }
    
    public SseEmitter subscribe(){
        User user = userService.getCurrentUser();
        Long userId = user.getId();
        // 여러개의 SseEmitter를 관리하기 위해, 중복되지 않은 key 값을 생성.
        // 이를 통해 데이터 복구, 덮어쓰기 방지를 할 수 있다.
        String eventId = generateEventId(userId);

        SseEmitter sseEmitter = emitterRepository.save(eventId, new SseEmitter(TIMEOUT));

        registerEmitterHandler(eventId, sseEmitter);

        sendToClient(eventId, sseEmitter,"알림 구독 성공 [userId = " + userId +"]");

        return sseEmitter;
    }

    // 실시간 인기 장소 알림 비동기 처리
    @Async
    public void broadcastHotPlace(Set<Long> connectedUserIds, String message){
        log.info("[broadcastHotPlace] 실행 쓰레드 : {}",Thread.currentThread().getName());
        connectedUserIds.forEach(userId -> {
            SseEmitter emitter = emitterRepository.findEmitterByUserId(userId);
            if (emitter != null) {
                try {
                    String eventId = generateEventId(userId);
                    emitter.send(SseEmitter.event()
                            .name("broadcast event")
                            .id(eventId)
                            .reconnectTime(RECONNECTION_TIMEOUT)
                            .data(message, MediaType.APPLICATION_JSON));
                    log.info("알림을 전송합니다. userId={}, payload={}", userId, message);
                } catch (IOException e) {
                    log.error("알림 전송에 실패하였습니다. userId={} - {}", userId, e.getMessage());
                }
            }
        });
    }

//    // 장소에 즐겨찾기 한 유저들한테 알림 비동기 처리
//    @Async("customTaskExecutor")
//    public void broadcastNewMeeting(Set<Long> userIds, String message){
//        log.info("[broadcastNewMeeting] 실행 쓰레드 : {}",Thread.currentThread().getName());
//        Map<String, SseEmitter> userEmitters = emitterRepository.findEmittersByUserId(userIds);
//        userEmitters.forEach((key, emitter)->{
//            Long userId = null;
//            if(emitter != null){
//                try {
//                    userId = Long.parseLong(key.split("_")[0]);
//                    String eventId = generateEventId(userId);
//                    emitter.send(SseEmitter.event()
//                            .name("NewMeeting Event")
//                            .id(eventId)
//                            .reconnectTime(RECONNECTION_TIMEOUT)
//                            .data(message, MediaType.APPLICATION_JSON));
//                    log.info("알림을 전송합니다. userId={} = {}", userId, message);
//                }catch (IOException e){
//                    log.error("알림 전송에 실패하였습니다. userId={} - {}", userId, e.getMessage());
//                }
//            }
//        });
//    }

    @Async("customTaskExecutor")
    public void broadcastNewMeeting(Set<Long> userIds, String message) {
        log.info("[broadcastNewMeeting] 실행 스레드: {}", Thread.currentThread().getName());

        // 🔹 모든 알림을 비동기 실행 (즉시 반환)
        userIds.forEach(userId ->
                CompletableFuture.runAsync(() -> sendSseNotification(userId, message), customTaskExecutor));
    }


    private void sendSseNotification(Long userId, String message) {
        SseEmitter emitter = emitterRepository.findEmitterByUserId(userId);
        if (emitter != null) {
            try {
                String eventId = generateEventId(userId);
                emitter.send(SseEmitter.event()
                        .name("NewMeeting Event")
                        .id(eventId)
                        .reconnectTime(RECONNECTION_TIMEOUT)
                        .data(message, MediaType.APPLICATION_JSON));
                log.info("알림 전송 완료 - userId={}, payload={}", userId, message);
            } catch (IOException e) {
                log.error("알림 전송 실패 - userId={}, error={}", userId, e.getMessage());
            }
        }
    }

    // 유저가 모임 참가 신청 (모임장 알림) | 모임 참가 신청 결과 알림 (참가 신청 유저 알림)
    @Async
    public void sendNotificationToClient(Long userId, String message){
        SseEmitter sseEmitter = emitterRepository.findEmitterByUserId(userId);
        if(sseEmitter != null){
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("apply-notification")
                        .data(message));
            }catch (IOException e){
                emitterRepository.deleteAllEmittersByUserId(userId);
            }
        }
    }

    // SseEmitter를 통해 클라이언트에게 이벤트를 전송하는 역할을 합니다.
    private void sendToClient(String eventId, SseEmitter sseEmitter, Object data){
        SseEmitter.SseEventBuilder event = getSseEvent(eventId, data);
        try{
            sseEmitter.send(event);
        }catch (IOException e){
            log.error("구독 실패, eventId ={}, {}", eventId, e.getMessage());
        }
    }

    // 이벤트 id와, data를 이용해서 SSE 이벤트 객체를 생성합니다.
    private SseEmitter.SseEventBuilder getSseEvent(String eventId, Object data){
        return SseEmitter.event()
                .id(eventId)
                .data(data)
                .reconnectTime(RECONNECTION_TIMEOUT);  // 클라이언트와 연결이 끊겼을 때 클라이언트가 서버와 재연결을 시도하기 전에 대기할 시간
                                                       // 해당 기능은 라이언트가 재연결 시도를 할 수 있도록 브라우저에게 힌트를 주는 역할
    }

    // SSE 연결이 종료되거나, 타임아웃되거나, 오류가 발생할 때 적절한 처리를 수행하도록 Emitter에 핸들러를 등록
    private void registerEmitterHandler(String eventId, SseEmitter sseEmitter){
        /*  SSE 연결이 정상적으로 종료되었을 때
           - 사용자가 브라우저를 닫거나 SSE 구독을 취소할 때
           - 서버에서 sseEmitter.complete()을 호출했을 때
        */
        sseEmitter.onCompletion(()->{
            log.info("연결이 끝났습니다. : eventId = {}", eventId);
            emitterRepository.deleteByEventId(eventId);
        });

        /*  SSE 연결이 타임아웃되었을 때
           - 클라이언트가 네트워크 문제로 오랜 시간 동안 응답을 받지 못했을 때
           - 서버에서 일정 시간 동안 데이터가 전송되지 않아 클라이언트가 반응하지 않을 때
           - SseEmitter가 설정된 timeout이 초과되었을 때
        */
        sseEmitter.onTimeout(()->{
            log.info("Timeout이 발생했습니다. : eventId={}", eventId);
            emitterRepository.deleteByEventId(eventId);
        });

        /*  SSE 연결 중 에러가 발생했을 때
            - 클라이언트가 갑자기 연결을 끊었을 때 (ERR_INCOMPLETE_CHUNKED_ENCODING)
            - 서버에서 SSE 메시지를 전송하는 중 네트워크 장애가 발생했을 때
            - 잘못된 데이터 형식이 전송되어 클라이언트가 파싱하지 못할 때
         */
        sseEmitter.onError((e) ->{
            log.info("에러가 발생했습니다. error={}, eventId={}", e.getMessage(),eventId);
            emitterRepository.deleteByEventId(eventId);
        });
    }

    private String generateEventId(Long userId) {
        return userId + "_" + clockHolder.mills();
    }

}
