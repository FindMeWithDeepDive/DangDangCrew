package findme.dangdangcrew.meeting.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeetingConfirmedUsersResponseDto {
    private Long userId;

    private String userName;
}
