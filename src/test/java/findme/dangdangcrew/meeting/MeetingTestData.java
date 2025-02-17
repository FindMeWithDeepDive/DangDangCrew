package findme.dangdangcrew.meeting;

import findme.dangdangcrew.meeting.dto.*;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public class MeetingTestData {

    /** ✅ 공통 User 생성 */
    public static User createUser() {
        User user = User.builder()
                .email("bugak@email.com")
                .name("이부각")
                .nickname("뿌야")
                .password("bugak123")
                .phoneNumber("01012341234")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    public static User createUser1() {
        User user = User.builder()
                .email("0sook@email.com")
                .name("이영숙")
                .nickname("앵이")
                .password("aeng123")
                .phoneNumber("01043214321")
                .build();
        ReflectionTestUtils.setField(user, "id", 2L);
        return user;
    }

    /** ✅ 공통 Place 생성 */
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

    public static Meeting createMeeting(MeetingStatus status) {
        return Meeting.builder()
                .id(1L)
                .meetingName("테스트용")
                .information("테스트용 모임입니다.")
                .maxPeople(5)
                .curPeople(2)
                .status(status)
                .createdAt(LocalDateTime.now())
                .place(createPlace())
                .build();
    }

    public static UserMeeting createUserMeeting(User user, Meeting meeting, UserMeetingStatus status) {
        UserMeeting userMeeting = UserMeeting.builder()
                .user(user)
                .meeting(meeting)
                .status(status)
                .build();
        ReflectionTestUtils.setField(userMeeting, "id", 1L);
        return userMeeting;
    }

    public static MeetingRequestDto createMeetingRequestDto() {
        MeetingRequestDto dto = new MeetingRequestDto();
        ReflectionTestUtils.setField(dto, "meetingName", "테스트 모임");
        ReflectionTestUtils.setField(dto, "information", "테스트용 모임 설명");
        ReflectionTestUtils.setField(dto, "maxPeople", 10);
        ReflectionTestUtils.setField(dto, "placeRequestDto", createPlaceRequestDto());
        return dto;
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

    public static MeetingUserStatusUpdateRequestDto createMeetingUserStatusUpdateRequestDto(User user, UserMeetingStatus status) {
        MeetingUserStatusUpdateRequestDto dto = new MeetingUserStatusUpdateRequestDto();
        ReflectionTestUtils.setField(dto, "userId", user.getId());
        ReflectionTestUtils.setField(dto, "status", status);
        return dto;
    }
}
