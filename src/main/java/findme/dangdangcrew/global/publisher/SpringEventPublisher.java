package findme.dangdangcrew.global.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringEventPublisher implements EventPublisher{
    private final ApplicationEventPublisher applicationEventPublisher;
    @Override
    public void publisher(Object event) {
        applicationEventPublisher.publishEvent(event);
    }
}
