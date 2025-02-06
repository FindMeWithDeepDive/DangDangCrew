package findme.dangdangcrew.notification.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NewMeetingEvent {
    private final Long targetUserId;
    private final Long meetingId;
}
