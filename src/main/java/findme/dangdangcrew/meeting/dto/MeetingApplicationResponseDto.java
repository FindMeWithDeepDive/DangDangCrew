package findme.dangdangcrew.meeting.dto;

import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeetingApplicationResponseDto {
    private Long userId;

    private String userName;

    private Long meetingId;

    private UserMeetingStatus status;
}
