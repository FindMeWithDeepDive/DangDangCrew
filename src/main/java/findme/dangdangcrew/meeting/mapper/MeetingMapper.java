package findme.dangdangcrew.meeting.mapper;

import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.place.domain.Place;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.List;

@Builder
@Component
public class MeetingMapper {
    public Meeting toEntity(MeetingRequestDto dto, Place place) {
        return Meeting.builder()
                .meetingName(dto.getMeetingName())
                .information(dto.getInformation())
                .maxPeople(dto.getMaxPeople())
                .curPeople(1)
                .place(place)
                .build();
    }

    public MeetingDetailResponseDto toDto(Meeting meeting) {
        return MeetingDetailResponseDto.builder()
                .meetingId(meeting.getId())
                .meetingName(meeting.getMeetingName())
                .information(meeting.getInformation())
                .maxPeople(meeting.getMaxPeople())
                .curPeople(meeting.getCurPeople())
                .status(meeting.getStatus())
                .createdAt(meeting.getCreatedAt())
                .build();
    }

    public List<MeetingBasicResponseDto> toListDto(List<Meeting> meetings){
        return meetings.stream()
                .map(meeting -> MeetingBasicResponseDto.builder()
                            .meetingId(meeting.getId())
                            .meetingName(meeting.getMeetingName())
                            .maxPeople(meeting.getMaxPeople())
                            .curPeople(meeting.getCurPeople())
                            .status(meeting.getStatus())
                            .createdAt(meeting.getCreatedAt())
                            .build())
            .toList();
    }

    public MeetingUserResponseDto toUserDto(UserMeeting userMeeting) {
        return MeetingUserResponseDto.builder()
                .meetingId(userMeeting.getMeeting().getId())
                .userId(userMeeting.getUser().getId())
                .userName(userMeeting.getUser().getName())
                .userMeetingStatus(userMeeting.getStatus())
                .build();
    }

    public List<MeetingUserResponseDto> toListUserDto(List<UserMeeting> userMeetings) {
        return userMeetings.stream().map(this::toUserDto).toList();
    }

    public List<MeetingUserResponseDto> toListMeetingUsersDto(List<UserMeeting> userMeetings) {
        return userMeetings.stream()
                .map(userMeeting -> MeetingUserResponseDto.builder()
                        .userId(userMeeting.getUser().getId())
                        .userName(userMeeting.getUser().getNickname())
                        .userMeetingStatus(userMeeting.getStatus())
                        .meetingId(userMeeting.getMeeting().getId())
                        .build())
                .toList();
    }
}
