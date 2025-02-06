package findme.dangdangcrew.meeting.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetingRequestDto {
    private String meetingName;

    private String information;

    private Integer maxPeople;

    private Integer curPeople;

    private LocalDateTime createdAt;
}
