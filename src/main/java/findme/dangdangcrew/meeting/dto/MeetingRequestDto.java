package findme.dangdangcrew.meeting.dto;

import lombok.Getter;

@Getter
public class MeetingRequestDto {
    private String meetingName;

    private String information;

    private Integer maxPeople;
}
