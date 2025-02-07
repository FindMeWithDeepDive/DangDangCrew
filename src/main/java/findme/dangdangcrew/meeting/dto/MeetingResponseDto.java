package findme.dangdangcrew.meeting.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MeetingResponseDto {
    private Long meetingId;

    private String meetingName;

    private String information;

    private Integer maxPeople;

    private Integer curPeople;

    private LocalDateTime createdAt;
}
