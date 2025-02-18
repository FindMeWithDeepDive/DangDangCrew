package findme.dangdangcrew.place.service;

import findme.dangdangcrew.global.dto.PagingResponseDto;
import findme.dangdangcrew.notification.domain.HotPlaceNotification;
import findme.dangdangcrew.notification.domain.NewMeetingNotification;
import findme.dangdangcrew.notification.domain.Notification;
import findme.dangdangcrew.notification.domain.NotificationEntity;
import findme.dangdangcrew.place.domain.FavoritePlace;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.FavoritePlaceResponseDto;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.repository.FavoritePlaceRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.parameters.P;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritePlaceServiceTest {

    @InjectMocks
    private FavoritePlaceService favoritePlaceService;

    @Mock
    private UserService userService;

    @Mock
    private PlaceService placeService;

    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;

    private User testUser;
    private Place testPlace;
    private PlaceRequestDto placeRequestDto;

    @BeforeEach
    void init() {
        testUser = User.builder()
                .email("test@gmail.com")
                .name("Test User")
                .nickname("Tester")
                .phoneNumber("010-1234-5678")
                .password("password")
                .build();
        ReflectionTestUtils.setField(testUser,"id",1L);

        testPlace = Place.builder()
                .id("12345")
                .placeName("Test Place")
                .x(37.1234)
                .y(127.5678)
                .build();

        placeRequestDto = new PlaceRequestDto("12345", "Test Place","37.1234","127.5678");
    }

    @Test
    @DisplayName("즐겨 찾기 장소를 등록합니다.")
    void registerFavoritePlace() {
        // given
        LocalDateTime now = LocalDateTime.now();
        FavoritePlace favoritePlace = FavoritePlace.builder()
                .user(testUser)
                .place(testPlace)
                .createdAt(now).build();

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(placeService.findOrCreatePlace(placeRequestDto)).thenReturn(testPlace);
        when(favoritePlaceRepository.existsByUserAndPlace(testUser, testPlace)).thenReturn(false);

        // when
        FavoritePlaceResponseDto result = favoritePlaceService.registerFavoritePlace(placeRequestDto);

        // then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.userId());
        assertEquals(testPlace.getId(), result.placeId());

        verify(favoritePlaceRepository, times(1)).save(any(FavoritePlace.class));
    }

    @Test
    @DisplayName("유저의 즐겨찾기 장소를 조회합니다.")
    void getAllFavoritePlace() {
        // given
        int page = 1;
        LocalDateTime now = LocalDateTime.now();
        Place place1 = Place.builder()
                .id("12345")
                .placeName("장소1")
                .x(12.345)
                .y(12.345)
                .build();
        Place place2 = Place.builder()
                .id("54321")
                .placeName("장소2")
                .x(54.321)
                .y(54.321)
                .build();

        FavoritePlace favoritePlace1 = FavoritePlace.builder()
                .user(testUser)
                .place(place1)
                .createdAt(now)
                .build();
        ReflectionTestUtils.setField(favoritePlace1,"id",1L);

        FavoritePlace favoritePlace2 = FavoritePlace.builder()
                .user(testUser)
                .place(place2)
                .createdAt(now.minusHours(2))
                .build();
        ReflectionTestUtils.setField(favoritePlace2,"id",2L);

        List<FavoritePlace> favoritePlaces = List.of(favoritePlace1, favoritePlace2);

        Page<FavoritePlace> favoritePlacePage = new PageImpl<>(
                favoritePlaces,
                PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                favoritePlaces.size()
        );

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(favoritePlaceRepository.findAllByUser(eq(testUser), any(Pageable.class))).thenReturn(favoritePlacePage);


        // when
        PagingResponseDto result = favoritePlaceService.getAllFavoritePlace(page);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getPages());
        assertFalse(result.getResults().isEmpty());

        // 첫 번째 결과 검증
        FavoritePlaceResponseDto result1 = (FavoritePlaceResponseDto) result.getResults().get(0);
        assertEquals(1L, result1.favoritePlaceId());
        assertEquals(1L, result1.userId());
        assertEquals("12345", result1.placeId());

        // 두 번째 결과 검증
        FavoritePlaceResponseDto result2 = (FavoritePlaceResponseDto) result.getResults().get(1);
        assertEquals(2L, result2.favoritePlaceId());
        assertEquals(1L, result2.userId());
        assertEquals("54321", result2.placeId());
    }
}