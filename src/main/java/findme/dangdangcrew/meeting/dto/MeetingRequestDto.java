package findme.dangdangcrew.meeting.dto;

import findme.dangdangcrew.place.dto.PlaceRequestDto;
import lombok.Getter;

@Getter
public class MeetingRequestDto {
    private String meetingName;

    private String information;

    private Integer maxPeople;

    private PlaceRequestDto placeRequestDto;
}
