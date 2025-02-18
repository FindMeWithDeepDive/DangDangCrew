package findme.dangdangcrew.meeting.dto;

import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import lombok.Getter;

@Getter
public class MeetingUserStatusUpdateRequestDto {
    private Long userId;
    private UserMeetingStatus status;
}
