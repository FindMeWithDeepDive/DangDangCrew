package findme.dangdangcrew.notification.service;

import findme.dangdangcrew.global.dto.PagingResponseDto;
import findme.dangdangcrew.notification.converter.NotificationConverterFactory;
import findme.dangdangcrew.notification.domain.*;
import findme.dangdangcrew.notification.repository.NotificationRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @InjectMocks
    private NotificationService notificationService;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationConverterFactory converterFactory;
    @Mock
    private UserService userService;

    private User testUser;
    private NotificationEntity testNotificationEntity;
    private Notification testNotification;

    @BeforeEach
    void init(){
        testUser = User.builder()
                .email("test@gmail.com")
                .name("test")
                .nickname("test")
                .phoneNumber("01011112222")
                .password("test1234")
                .build();
        ReflectionTestUtils.setField(testUser,"id",1L);

        testNotificationEntity = NotificationEntity.builder()
                .targetUserId(1L)
                .message("새로운 미팅이 생성되었습니다!")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .meetingId(200L)
                .notificationType(NotificationType.MEETING_CREATED)
                .build();
        ReflectionTestUtils.setField(testNotificationEntity,"id",1L);

        testNotification = new NewMeetingNotification(
                1L,
                "NewMeeting message",
                false,
                1L,
                "12345",
                LocalDateTime.now());
    }

    @Test
    @DisplayName("사용자의 모든 알림을 조회합니다.")
    void getUserNotifications() {
        // given
        int page = 1;
        Long notificationId1 = 1L;
        Long notificationId2 = 2L;
        Long targetUserId = 1L;
        String message1 = "NewMeeting message";
        String message2 = "HotPlace Message";
        boolean isRead1 = false;
        boolean isRead2 = true;
        LocalDateTime now = LocalDateTime.now();
        Long meetingId = 100L;
        String placeId = "12345";
        NotificationType notificationType1 = NotificationType.MEETING_CREATED;
        NotificationType notificationType2 = NotificationType.HOT_PLACE;

        // 두 개의 NotificationEntity 생성
        List<NotificationEntity> notificationEntities = List.of(
                NotificationEntity.builder()
                        .targetUserId(targetUserId)
                        .message(message1)
                        .isRead(isRead1)
                        .createdAt(now)
                        .meetingId(meetingId)
                        .notificationType(notificationType1)
                        .build(),
                NotificationEntity.builder()
                        .targetUserId(targetUserId)
                        .message(message2)
                        .isRead(isRead2)
                        .createdAt(now.minusHours(1))
                        .placeId(placeId)
                        .notificationType(notificationType2)
                        .build()
        );

        Page<NotificationEntity> notificationPage = new PageImpl<>(
                notificationEntities,
                PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                notificationEntities.size()
        );

        // 두 개의 Notification을 변환한 객체
        Notification notification1 = new NewMeetingNotification(
                notificationId1,
                targetUserId,
                message1,
                isRead1,
                meetingId,
                placeId,
                now);

        Notification notification2 = new HotPlaceNotification(
                notificationId2,
                targetUserId,
                message2,
                isRead2,
                placeId,
                now.minusHours(2));

        // Mock 설정
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findByTargetUserId(eq(testUser.getId()), any(Pageable.class)))
                .thenReturn(notificationPage);
        when(converterFactory.convert(eq(notificationEntities.get(0)))).thenReturn(notification1);
        when(converterFactory.convert(eq(notificationEntities.get(1)))).thenReturn(notification2);

        // when
        PagingResponseDto responseDto = notificationService.getUserNotifications(page);

        // then
        assertNotNull(responseDto);
        assertEquals(2, responseDto.getTotal());
        assertEquals(1, responseDto.getPages());
        assertFalse(responseDto.getResults().isEmpty());

        // 첫 번째 결과 검증
        Notification result1 = (Notification) responseDto.getResults().get(0);
        assertEquals("NewMeeting message", result1.getMessage());
        assertEquals(1L, result1.getTargetUserId());
        assertEquals(100L, ((NewMeetingNotification) result1).getMeetingId());

        // 두 번째 결과 검증
        Notification result2 = (Notification) responseDto.getResults().get(1);
        assertEquals("HotPlace Message", result2.getMessage());
        assertEquals(1L, result2.getTargetUserId());
        assertEquals("12345", ((HotPlaceNotification) result2).getPlaceId());
    }

    // 단순한 객체 사용으로 init에서 생성 (맞는 논리인지는 모르겠음)
    @Test
    @DisplayName("알림을 읽음 처리 합니다.")
    void readNotificationTest() {
        // given
        Long notificationId = 1L;
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.ofNullable(testNotificationEntity));

        // when
        Long result = notificationService.readNotification(notificationId);

        // then
        assertNotNull(result);
        assertEquals(notificationId, result);
        assertTrue(testNotificationEntity.isRead());
        verify(notificationRepository,times(1)).findById(notificationId);
    }

    @Test
    @DisplayName("알림을 저장합니다.")
    void saveNotificationTest() {
        // given
        Long targetUserId = 1L;
        String message = "새로운 미팅이 생성되었습니다!";
        boolean isRead = false;
        LocalDateTime now = LocalDateTime.now();
        Long meetingId = 100L;
        String placeId = "12345";
        NotificationType notificationType = NotificationType.MEETING_CREATED;

        Notification notification = new NewMeetingNotification(
                targetUserId,
                message,
                isRead,
                meetingId,
                placeId,
                now
        );

        NotificationEntity notificationEntity = NotificationEntity.builder()
                .targetUserId(targetUserId)
                .message(message)
                .isRead(isRead)
                .createdAt(now)
                .meetingId(meetingId)
                .notificationType(notificationType)
                .build();
        when(converterFactory.convertToEntity(notification)).thenReturn(notificationEntity);
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(notificationEntity);

        // when
        notificationService.saveNotification(notification);

        // then
        verify(converterFactory, times(1)).convertToEntity(notification);
        verify(notificationRepository, times(1)).save(notificationEntity);
    }

    @Test
    @DisplayName("여러 개의 알림을 저장한다.")
    void saveAllNotificationsTest() {
        // given
        Long targetUserId = 1L;
        String message1 = "첫 번째 미팅 알림";
        String message2 = "두 번째 미팅 알림";
        boolean isRead = false;
        LocalDateTime now = LocalDateTime.now();
        Long meetingId1 = 100L;
        Long meetingId2 = 200L;
        String placeId = "12345";
        NotificationType notificationType = NotificationType.MEETING_CREATED;

        // Notification 객체 2개 생성
        List<Notification> notifications = List.of(
                new NewMeetingNotification(targetUserId, message1, isRead, meetingId1, placeId, now),
                new NewMeetingNotification(targetUserId, message2, isRead, meetingId2, placeId, now)
        );

        // 변환될 NotificationEntity 리스트
        List<NotificationEntity> notificationEntities = List.of(
                NotificationEntity.builder()
                        .targetUserId(targetUserId)
                        .message(message1)
                        .isRead(isRead)
                        .createdAt(now)
                        .meetingId(meetingId1)
                        .notificationType(notificationType)
                        .build(),
                NotificationEntity.builder()
                        .targetUserId(targetUserId)
                        .message(message2)
                        .isRead(isRead)
                        .createdAt(now)
                        .meetingId(meetingId2)
                        .notificationType(notificationType)
                        .build()
        );

        // Mock 동작 설정
        when(converterFactory.convertToEntity(notifications.get(0))).thenReturn(notificationEntities.get(0));
        when(converterFactory.convertToEntity(notifications.get(1))).thenReturn(notificationEntities.get(1));
        when(notificationRepository.saveAll(notificationEntities)).thenReturn(notificationEntities);

        // when
        notificationService.saveAllNotification(notifications);

        // then
        verify(converterFactory, times(1)).convertToEntity(notifications.get(0));
        verify(converterFactory, times(1)).convertToEntity(notifications.get(1));
        verify(notificationRepository, times(1)).saveAll(notificationEntities);
    }
}