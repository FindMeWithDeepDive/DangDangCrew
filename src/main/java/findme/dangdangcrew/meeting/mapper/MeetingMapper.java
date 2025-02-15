package findme.dangdangcrew.meeting.mapper;

import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.user.entity.User;
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
                            .build())
            .toList();
    }

    public MeetingApplicationResponseDto toApplicationDto(UserMeeting userMeeting) {
        return MeetingApplicationResponseDto.builder()
                .meetingId(userMeeting.getMeeting().getId())
                .userId(userMeeting.getUser().getId())
                .userName(userMeeting.getUser().getName())
                .status(userMeeting.getStatus())
                .build();
    }

    public List<MeetingApplicationResponseDto> toListApplicationDto(List<UserMeeting> userMeetings) {
        return userMeetings.stream()
                .map(this::toApplicationDto)
                .toList();
    }

    public List<MeetingUsersResponseDto> toListMeetingUsersDto(List<User> users) {
        return users.stream().map(user -> MeetingUsersResponseDto.builder()
                .userId(user.getId())
                .userName(user.getNickname())
                .build())
                .toList();
    }
}
