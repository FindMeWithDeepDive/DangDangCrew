package findme.dangdangcrew.notification.event;

import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.service.NotificationService;
import findme.dangdangcrew.place.domain.FavoritePlace;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.repository.FavoritePlaceRepository;
import findme.dangdangcrew.sse.service.SseService;
import findme.dangdangcrew.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;

    @Mock
    private SseService sseService;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    private User testUser1;
    private User testUser2;


    @BeforeEach
    void init(){
        testUser1 = User.builder()
                .email("test1@gmail.com")
                .name("Test User")
                .nickname("Tester1")
                .phoneNumber("010-1234-5679")
                .password("password")
                .build();
        ReflectionTestUtils.setField(testUser1,"id",1L);

        testUser2 = User.builder()
                .email("test2@gmail.com")
                .name("Test User")
                .nickname("Tester2")
                .phoneNumber("010-1234-5688")
                .password("password")
                .build();
        ReflectionTestUtils.setField(testUser2,"id",2L);
    }

    @Test
    @DisplayName("새로운 모임이 생성되어 모임 장소를 즐겨찾기한 유저에게 알림을 전송합니다.")
    void handleNewMeetingNotificationTest() {
        // given
        NewMeetingEvent event = new NewMeetingEvent("placeTest", 1L, "12345");
        Place place = Place.builder().build();
        LocalDateTime now = LocalDateTime.now();

        List<FavoritePlace> favoritePlaces = List.of(
                FavoritePlace.builder()
                        .user(testUser1)
                        .place(place)
                        .createdAt(now)
                        .build(),
                FavoritePlace.builder()
                        .user(testUser2)
                        .place(place)
                        .createdAt(now.minusHours(2))
                        .build());

        when(favoritePlaceRepository.findAllByPlaceId(event.placeId())).thenReturn(favoritePlaces);


        // when
        notificationEventListener.handleNewMeetingNotification(event);

        // then
        verify(favoritePlaceRepository, times(1)).findAllByPlaceId(anyString());
        verify(notificationService, times(1)).saveAllNotification(anyList());
        verify(sseService, times(1)).broadcastNewMeeting(anySet(), anyString());
    }

    @Test
    @DisplayName("핫 플레이스 알림을 접속한 유저들에게 전송합니다.")
    void handleHotPlaceNotificationTest() {
        // given
        HotPlaceEvent event = new HotPlaceEvent("testPlace","12345");
        Set<Long> connectedUsers = Set.of(1L, 2L, 3L);
        when(sseService.getConnectedUserIds()).thenReturn(connectedUsers);

        // when
        notificationEventListener.handleHotPlaceNotification(event);

        // then
        verify(notificationService, times(1)).saveAllNotification(anyList());
        verify(sseService, times(1)).broadcastHotPlace(anySet(), anyString());

    }

    @Test
    @DisplayName("참가 신청 알림을 전송합니다.")
    void handleApplyNotificationTest() {
        // given
        ApplyEvent event = new ApplyEvent(1L, "test", 2L,1L, "testMeeting");
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // when
        notificationEventListener.handleApplyNotification(event);

        // then
        verify(notificationService, times(1)).saveNotification(notificationCaptor.capture());
        verify(sseService, times(1)).sendNotificationToClient(anyLong(), anyString());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(1L, capturedNotification.getTargetUserId());
        assertEquals("test 님께서 testMeeting 에 참여 하고 싶어 합니다.", capturedNotification.getMessage());

    }

    @Test
    @DisplayName("참가 신청을 승낙하는 알림을 전송합니다.")
    void handleLeaderActionNotificationTest_Accept() {
        // given
        LeaderActionEvent event = new LeaderActionEvent(2L, 1L, "testMeeting",LeaderActionType.JOIN_ACCEPTED);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // when
        notificationEventListener.handleLeaderActionNotification(event);

        // then
        verify(notificationService, times(1)).saveNotification(notificationCaptor.capture());
        verify(sseService, times(1)).sendNotificationToClient(anyLong(), anyString());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(2L, capturedNotification.getTargetUserId());
        assertEquals("[testMeeting] 모임 참가 신청이 승낙되었습니다.", capturedNotification.getMessage());
    }

    @Test
    @DisplayName("참가 신청을 거절하는 알림을 전송합니다.")
    void handleLeaderActionNotificationTest_Reject() {
        // given
        LeaderActionEvent event = new LeaderActionEvent(2L, 1L, "testMeeting",LeaderActionType.JOIN_REJECTED);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // when
        notificationEventListener.handleLeaderActionNotification(event);

        // then
        verify(notificationService, times(1)).saveNotification(notificationCaptor.capture());
        verify(sseService, times(1)).sendNotificationToClient(anyLong(), anyString());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(2L, capturedNotification.getTargetUserId());
        assertEquals("[testMeeting] 모임 참가 신청이 반려되었습니다.", capturedNotification.getMessage());
    }

    @Test
    @DisplayName("모임 강퇴 알림을 전송합니다.")
    void handleLeaderActionNotificationTest_Kick_Out() {
        // given
        LeaderActionEvent event = new LeaderActionEvent(2L, 1L, "testMeeting",LeaderActionType.KICK_OUT);
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // when
        notificationEventListener.handleLeaderActionNotification(event);

        // then
        verify(notificationService, times(1)).saveNotification(notificationCaptor.capture());
        verify(sseService, times(1)).sendNotificationToClient(anyLong(), anyString());

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(2L, capturedNotification.getTargetUserId());
        assertEquals("[testMeeting] 모임에서 강퇴되었습니다.", capturedNotification.getMessage());
    }
}