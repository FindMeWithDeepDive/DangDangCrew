package findme.dangdangcrew.place.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import findme.dangdangcrew.global.config.JwtTokenProvider;
import findme.dangdangcrew.place.domain.FavoritePlace;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.repository.FavoritePlaceRepository;
import findme.dangdangcrew.place.repository.PlaceRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FavoritePlaceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private FavoritePlaceRepository favoritePlaceRepository;

    private User testLeaderUser;
    private User testUser;
    private Place testPlace;

    @BeforeEach
    void init(){
        testLeaderUser = User.builder()
                .email("leader@gmail.com")
                .name("leader")
                .nickname("leader")
                .phoneNumber("01011112222")
                .password("test1234")
                .build();
        userRepository.saveAndFlush(testLeaderUser);
        testUser = User.builder()
                .email("user@gmail.com")
                .name("user")
                .nickname("user")
                .phoneNumber("01011113333")
                .password("test1234")
                .build();
        userRepository.saveAndFlush(testUser);

        testPlace = Place.builder()
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
        testPlace = placeRepository.saveAndFlush(testPlace);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        favoritePlaceRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        placeRepository.deleteAllInBatch();
    }

    private String generateTestToken(User user) {
        return jwtTokenProvider.generateAccessToken(user.getEmail());
    }

    @Test
    @DisplayName("유저가 장소에 즐겨찾기를 등록합니다.")
    void registerFavoritePlaceTest() throws Exception {
        // given
        String testToken = generateTestToken(testUser);
        String placeName = "원앤온리";
        PlaceRequestDto placeRequestDto = new PlaceRequestDto("217787831", "원앤온리", "126.319192490757", "33.2392223486155");

        // when
        // then
        mockMvc.perform(post("/api/v1/favorite-place")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(placeRequestDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.msg").value(placeName+" 장소를 즐겨찾기 하였습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("유저가 즐겨찾기한 장소들을 조회합니다.")
    void getAllFavoritePlace() throws Exception {
        // given
        String testToken = generateTestToken(testUser);
        String page = "1";
        LocalDateTime now = LocalDateTime.now();
        FavoritePlace favoritePlace = FavoritePlace.builder()
                .user(testUser)
                .place(testPlace)
                .createdAt(now)
                .build();

        favoritePlaceRepository.save(favoritePlace);

        // when
        // then
        mockMvc.perform(get("/api/v1/favorite-place")
                        .header("Authorization", "Bearer " + testToken)
                        .param("page", page)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.msg").value("유저가 즐겨찾기한 장소를 성공적으로 조회하였습니다."),
                        jsonPath("$.data.pages").value(1), // 페이징 검증 (추가적으로 수정 가능)
                        jsonPath("$.data.total").value(1), // 총 즐겨찾기한 장소 개수 검증
                        jsonPath("$.data.results[0].placeId").value("217787831"),
                        jsonPath("$.data.results[0].placeName").value("원앤온리"),
                        jsonPath("$.data.results[0].userId").value(testUser.getId()),
                        jsonPath("$.data.results[0].addressName").value(testPlace.getAddressName()),
                        jsonPath("$.data.results[0].favoritePlaceId").value(favoritePlace.getId())
                ).andDo(print());
    }
}