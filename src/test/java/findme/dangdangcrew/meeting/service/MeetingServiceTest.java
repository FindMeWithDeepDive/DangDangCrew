package findme.dangdangcrew.meeting.service;

import findme.dangdangcrew.chat.service.ChatRoomService;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.global.publisher.EventPublisher;
import findme.dangdangcrew.meeting.MeetingTestData;
import findme.dangdangcrew.meeting.dto.MeetingBasicResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingDetailResponseDto;
import findme.dangdangcrew.meeting.dto.MeetingRequestDto;
import findme.dangdangcrew.meeting.dto.MeetingUserResponseDto;
import findme.dangdangcrew.meeting.entity.Meeting;
import findme.dangdangcrew.meeting.entity.UserMeeting;
import findme.dangdangcrew.meeting.entity.enums.MeetingStatus;
import findme.dangdangcrew.meeting.entity.enums.UserMeetingStatus;
import findme.dangdangcrew.meeting.mapper.MeetingMapper;
import findme.dangdangcrew.meeting.repository.MeetingRepository;
import findme.dangdangcrew.notification.event.NewMeetingEvent;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.service.PlaceService;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetingServiceTest {

    @InjectMocks
    private MeetingService meetingService;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MeetingMapper meetingMapper;

    @Mock
    private UserMeetingService userMeetingService;

    @Mock
    private UserService userService;

    @Mock
    private PlaceService placeService;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private EventPublisher eventPublisher;

    private Meeting meeting;
    private User user;
    private Place place;
    private UserMeeting userMeeting;
    private MeetingRequestDto meetingRequestDto;
    private MeetingDetailResponseDto meetingDetailResponseDto;
    private MeetingUserResponseDto meetingUserResponseDto;

    @BeforeEach
    void setup() {
        user = MeetingTestData.createUser();
        place = MeetingTestData.createPlace();
        meetingRequestDto = MeetingTestData.createMeetingRequestDto();
        meeting = MeetingTestData.createMeeting(MeetingStatus.IN_PROGRESS);
        userMeeting = MeetingTestData.createUserMeeting(user, meeting, UserMeetingStatus.LEADER);
        meetingDetailResponseDto = MeetingTestData.createMeetingDetailResponseDto();
        meetingUserResponseDto = MeetingTestData.createMeetingUserResponseDto(userMeeting);
    }

    @Test
    @DisplayName("[✅모임 생성] 모임을 성공적으로 생성합니다.")
    void createMeeting_Success(){
        when(userService.getCurrentUser()).thenReturn(user);
        when(placeService.findOrCreatePlace(any())).thenReturn(place);
        when(meetingMapper.toEntity(any(), any())).thenReturn(meeting);
        when(meetingRepository.save(any())).thenReturn(meeting);
        when(userMeetingService.createUserMeeting(any(), any(), any())).thenReturn(userMeeting);
        when(meetingMapper.toUserDto(any())).thenReturn(meetingUserResponseDto);

        doNothing().when(chatRoomService).createChatRoom(any(Meeting.class));
        doNothing().when(eventPublisher).publisher(any(NewMeetingEvent.class));

        MeetingUserResponseDto response = meetingService.create(meetingRequestDto);

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(meeting.getId(), response.getMeetingId());
    }

    @Test
    @DisplayName("[❌모임 생성] 최대 인원 초과로 인해 모임 생성에 실패합니다.")
    void createMeeting_InvalidMaxPeople_Fail() {
        MeetingRequestDto dto = new MeetingRequestDto();
        ReflectionTestUtils.setField(dto, "maxPeople", 11);

        when(placeService.findOrCreatePlace(any())).thenReturn(MeetingTestData.createPlace());

        CustomException exception = assertThrows(CustomException.class, () -> meetingService.create(dto));

        assertEquals(ErrorCode.INVALID_MEETING_CAPACITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("[✅모임 조회] 모임 상세 정보를 정상적으로 조회합니다.")
    void readByMeetingId_Success() {
        when(meetingRepository.findByIdAndStatusIn(eq(meeting.getId()), any())).thenReturn(Optional.of(meeting));
        when(meetingMapper.toDto(any())).thenReturn(meetingDetailResponseDto);

        MeetingDetailResponseDto response = meetingService.readByMeetingId(meeting.getId());

        assertNotNull(response);
    }

    @Test
    @DisplayName("[❌모임 조회] 존재하지 않는 모임 조회 시 예외를 발생시킵니다.")
    void readByMeetingId_NotFound_Fail() {
        Long nonExistingMeetingId = 999L;
        when(meetingRepository.findByIdAndStatusIn(eq(nonExistingMeetingId), any()))
                .thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> meetingService.readByMeetingId(nonExistingMeetingId));

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("[✅모임 상태 업데이트] - 현재 인원이 최대 인원과 같으면 상태가 COMPLETED가 된다.")
    void updateMeetingStatus_Completed() {
        ReflectionTestUtils.setField(meeting, "curPeople", meeting.getMaxPeople());
        meetingService.updateMeetingStatus(meeting);
        assertEquals(MeetingStatus.COMPLETED, meeting.getStatus());
    }

    @Test
    @DisplayName("[✅모임 상태 업데이트] - 현재 인원이 최대 인원보다 적으면 상태가 IN_PROGRESS가 된다.")
    void updateMeetingStatus_InProgress() {
        ReflectionTestUtils.setField(meeting, "curPeople", meeting.getMaxPeople() - 1);

        meetingService.updateMeetingStatus(meeting);

        assertEquals(MeetingStatus.IN_PROGRESS, meeting.getStatus());
    }

    @Test
    @DisplayName("[✅모임 확정자 조회] - 모임 확정자 목록을 성공적으로 조회한다.")
    void readAllConfirmed_Success() {
        when(meetingRepository.findByIdAndStatusIn(anyLong(), any())).thenReturn(Optional.of(meeting));
        when(userMeetingService.findConfirmedByMeetingId(meeting)).thenReturn(List.of(userMeeting));
        when(meetingMapper.toListUserDto(any())).thenReturn(List.of(meetingUserResponseDto));

        List<MeetingUserResponseDto> result = meetingService.readAllConfirmed(meeting.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(meetingMapper).toListUserDto(any());
    }

    @Test
    @DisplayName("[❌모임 확정자 조회] - 존재하지 않는 모임을 조회 시 예외 발생")
    void readAllConfirmed_NotFound_Fail() {
        Long nonExistingMeetingId = 999L;
        when(meetingRepository.findByIdAndStatusIn(eq(nonExistingMeetingId), any()))
                .thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> meetingService.readAllConfirmed(nonExistingMeetingId));

        assertEquals(ErrorCode.MEETING_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("[✅장소별 모임 조회] - 특정 장소의 모임 목록을 조회한다.")
    void findMeetingsByPlaceId_Success() {
        String placeId = "place123";
        when(meetingRepository.findAllByPlace_IdAndStatusIn(eq(placeId), any()))
                .thenReturn(List.of(meeting));
        when(meetingMapper.toListDto(any())).thenReturn(List.of());

        List<MeetingBasicResponseDto> result = meetingService.findMeetingsByPlaceId(placeId);

        assertNotNull(result);
        verify(meetingRepository).findAllByPlace_IdAndStatusIn(eq(placeId), any());
    }

    @Test
    @DisplayName("[✅내 모임 조회] - 현재 사용자의 모임 목록을 조회한다.")
    void findMyMeetings_Success() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(userMeetingService.findUserMeetingByUser(user)).thenReturn(List.of(userMeeting));
        when(meetingRepository.findAllByIdIn(anyList())).thenReturn(List.of(meeting));
        when(meetingMapper.toListDto(any())).thenReturn(List.of());

        List<MeetingBasicResponseDto> result = meetingService.findMyMeetings();

        assertNotNull(result);
        verify(meetingRepository).findAllByIdIn(anyList());
    }

    @Test
    @DisplayName("[✅내 모임 조회] - 사용자가 참여한 모임이 없을 경우 빈 리스트 반환")
    void findMyMeetings_Empty() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(userMeetingService.findUserMeetingByUser(user)).thenReturn(List.of());

        List<MeetingBasicResponseDto> result = meetingService.findMyMeetings();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
