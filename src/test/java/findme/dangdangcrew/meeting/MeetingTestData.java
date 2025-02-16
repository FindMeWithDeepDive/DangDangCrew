package findme.dangdangcrew.meeting;

import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.user.entity.User;

import java.time.LocalDateTime;

public class MeetingTestData {

    public static User createUser() {
        return User.builder()
                .email("bugak@email.com")
                .name("이부각")
                .nickname("뿌야")
                .password("bugak123")
                .phoneNumber("01012341234")
                .build();
    }

    public static Place createPlace() {
        return Place.builder()
                .id("217787831")
                .placeName("원앤온리")
                .x(126.319192490757)
                .y(33.2392223486155)
                .categoryGroupCode("CE7")
                .categoryGroupName("카페")
                .phone("064-794-0117")
                .placeUrl("http://place.map.kakao.com/217787831")
                .addressName("제주특별자치도 서귀포시 안덕면 사계리 86")
                .roadAddressName("제주특별자치도 서귀포시 안덕면 산방로 141")
                .build();
    }

    public static PlaceRequestDto createPlaceRequestDto() {
        return new PlaceRequestDto("217787831", "원앤온리", "126.319192490757", "33.2392223486155");
    }

    public static Meeting createMeeting(Place place) {
        return Meeting.builder()
                .id(1L)
                .meetingName("테스트용")
                .information("테스트용 모임입니다.")
                .maxPeople(5)
                .curPeople(2)
                .status(MeetingStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .place(place)
                .build();
    }

    public static UserMeeting createUserMeeting(User user, Meeting meeting) {
        return UserMeeting.builder()
                .id(1L)
                .user(user)
                .meeting(meeting)
                .status(UserMeetingStatus.LEADER)
                .build();
    }

    public static MeetingRequestDto createMeetingRequestDto(PlaceRequestDto placeRequestDto) {
        return MeetingRequestDto.builder()
                .meetingName("테스트용")
                .information("테스트용 모임입니다.")
                .maxPeople(5)
                .placeRequestDto(placeRequestDto)
                .build();
    }

    public static MeetingDetailResponseDto createMeetingDetailResponseDto() {
        return MeetingDetailResponseDto.builder()
                .meetingId(1L)
                .meetingName("테스트용")
                .information("테스트용 모임입니다.")
                .maxPeople(5)
                .curPeople(2)
                .status(MeetingStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MeetingUserResponseDto createMeetingUserResponseDto(UserMeeting userMeeting) {
        return MeetingUserResponseDto.builder()
                .userId(userMeeting.getUser().getId())
                .userName(userMeeting.getUser().getNickname())
                .meetingId(userMeeting.getMeeting().getId())
                .userMeetingStatus(userMeeting.getStatus())
                .build();
    }
}
