package findme.dangdangcrew.meeting.dto;

import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MeetingDetailResponseDto {
    private Long meetingId;

    private String meetingName;

    private String information;

    private Integer maxPeople;

    private Integer curPeople;

    private MeetingStatus status;

    private LocalDateTime createdAt;
}
