package findme.dangdangcrew.sse.controller;

import findme.dangdangcrew.sse.service.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "[SSE] SSE API", description = "SSE 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
@Log4j2
public class SseController {
    private final SseService sseService;

    // http://localhost:8080/api/v1/sse/subscribe
    @Operation(summary = "알림 구독", description = "헤더의 토큰 기반의 유저가 알림을 구독합니다.")
    @GetMapping(value = "/subscribe",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(){
        return sseService.subscribe();
    }
}
