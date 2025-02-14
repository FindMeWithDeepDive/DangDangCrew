package findme.dangdangcrew.meeting.dto;

import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingApplicationUpdateRequestDto {
    private Long userId;

    private UserMeetingStatus status;
}
