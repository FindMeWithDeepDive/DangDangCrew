package findme.dangdangcrew.meeting.dto;

import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeetingUsersResponseDto {
    private Long userId;

    private String userName;
}
