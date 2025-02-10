package findme.dangdangcrew.meeting.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeetingBasicResponseDto {
    private Long meetingId;

    private String meetingName;

    private Integer maxPeople;

    private Integer curPeople;
}
