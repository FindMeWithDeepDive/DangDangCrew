package findme.dangdangcrew.meeting.mapper;

import findme.dangdangcrew.meeting.dto.MeetingListResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingRequestDto;
import findme.dangdangcrew.meeting.dto.MeetingDetailResponseDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

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

    public MeetingDetailResponseDto toDto(Meeting meeting) {
        return MeetingDetailResponseDto.builder()
                .meetingId(meeting.getId())
                .meetingName(meeting.getMeetingName())
                .information(meeting.getInformation())
                .maxPeople(meeting.getMaxPeople())
                .curPeople(meeting.getCurPeople())
                .createdAt(meeting.getCreatedAt())
                .build();
    }

    public List<MeetingListResponseDto> toListDto(List<Meeting> meetings){
        return meetings.stream()
                .map(meeting -> MeetingListResponseDto.builder()
                            .meetingId(meeting.getId())
                            .meetingName(meeting.getMeetingName())
                            .maxPeople(meeting.getMaxPeople())
                            .curPeople(meeting.getCurPeople())
                            .build())
            .toList();
    }
}
