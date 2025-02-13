package findme.dangdangcrew.notification.controller;

import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.notification.event.ApplyEvent;
import findme.dangdangcrew.notification.event.HotPlaceEvent;
import findme.dangdangcrew.notification.event.NewMeetingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class EventTestController {

    private final EventPublisher eventPublisher;

    @GetMapping("/apply")
    public String testApplyApi(){
        eventPublisher.publisher(new ApplyEvent(1L,"강형준",2L,1L,"강남역 카페 모임"));

        return "ok";
    }


    @GetMapping("/hot-place")
    public String testHotPlaceApi(){
        eventPublisher.publisher(new HotPlaceEvent("강남역 카페","12345"));

        return "ok";
    }


    @GetMapping("/new-meeting")
    public String testNewMeetingApi(){
        eventPublisher.publisher(new NewMeetingEvent("강남역 카페",1L,"12345"));

        return "ok";
    }
}
