package findme.dangdangcrew.meeting.mapper;

import findme.dangdangcrew.meeting.dto.MeetingRequestDto;
import findme.dangdangcrew.meeting.dto.MeetingResponseDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Builder
@Component
public class MeetingMapper {
    public Meeting toEntity(MeetingRequestDto dto) {
        return Meeting.builder()
                .meetingName(dto.getMeetingName())
                .information(dto.getInformation())
                .maxPeople(dto.getMaxPeople())
                .curPeople(dto.getCurPeople())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public MeetingResponseDto toDto(Meeting meeting) {
        return MeetingResponseDto.builder()
                .meetingId(meeting.getMeetingId())
                .meetingName(meeting.getMeetingName())
                .information(meeting.getInformation())
                .maxPeople(meeting.getMaxPeople())
                .curPeople(meeting.getCurPeople())
                .createdAt(meeting.getCreatedAt())
                .build();
    }
}
