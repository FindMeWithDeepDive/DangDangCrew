package findme.dangdangcrew.place.service;

import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private WebClient kakaoWebClient;

    @InjectMocks
    private PlaceService placeService;


    @Test
    @DisplayName("DB에 장소가 존재할 경우 그대로 반환")
    void findOrCreatePlace_FindTest() {
        // given
        PlaceRequestDto requestDto = new PlaceRequestDto("12345", "testPlace","12.345","12.345");
        Place existingPlace = Place.builder().id("12345").placeName("testPlace").x(12.345).y(12.345).build();

        when(placeRepository.findById("12345")).thenReturn(Optional.ofNullable(existingPlace));

        // when
        Place result = placeService.findOrCreatePlace(requestDto);

        // then
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertEquals("testPlace", result.getPlaceName());
        assertEquals(12.345, result.getX());
        assertEquals(12.345, result.getY());
        verify(placeRepository, never()).save(any());
    }

    @Test
    @DisplayName("DB에 장소가 존재하지 않을 경우 Kakao Api 호출 후 저장")
    void findOrCreatePlace_CreateTest() {
        // given
        PlaceRequestDto requestDto = new PlaceRequestDto("12345", "testPlace","12.345","12.345");
        Place newPlace = Place.builder().id("12345").placeName("testPlace").x(12.345).y(12.345).build();

        when(placeRepository.findById("12345")).thenReturn(Optional.empty());

        Map<String, Object> testResponse = new HashMap<>();
        List<Map<String, Object>> documents = new ArrayList<>();
        Map<String, Object> placeData = Map.of(
                "id", "12345",
                "place_name", "testPlace",
                "x","12.345",
                "y","12.345"
        );
        documents.add(placeData);
        testResponse.put("documents", documents);

        // WebClient 요청 Mocking
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        // WebClient 동작 정의
        // 1. WebClient.get() 이 호출이 되면 RequestHeadersUriSpec을 반환
        when(kakaoWebClient.get()).thenReturn(requestHeadersUriSpecMock);
        // 2. uri() 가 호출이 되면 RequestHeaderSpec을 반환, 파라미터로 전달된 URI를 무시
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        // 3. header("Authorization", "KakaoAK test-api-key") 가 호출되면, 동일한 requestHeadersSpecMock 을 반환
        when(requestHeadersSpecMock.header(eq("Authorization"), anyString())).thenReturn(requestHeadersSpecMock);
        // 4. retrieve() 호출 시, ResponseSpec 객체 (responseSpecMock) 를 반환하도록 설정.
        //    이 단계에서 WebClient는 실제 HTTP 요청을 보내지 않고, 가짜 응답을 받을 준비를 함.
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        // 5. .bodyToMono(Map.class)가 호출되면, 실제 API 응답 대신 가짜 응답 (testResponse) 을 반환하도록 설정.
        when(responseSpecMock.bodyToMono(Map.class)).thenReturn(Mono.just(testResponse));

        // save 동작 정의
        when(placeRepository.save(any(Place.class))).thenReturn(newPlace);

        // when
        Place result = placeService.findOrCreatePlace(requestDto);

        // then
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertEquals("testPlace", result.getPlaceName());
        assertEquals(12.345, result.getX());
        assertEquals(12.345, result.getY());
        verify(placeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("장소 ID 로 장소를 조회합니다.")
    void findPlaceByPlaceIdTest() {
        // given
        String placeId = "12345";
        Place place = Place.builder().id(placeId).placeName("testPlace").x(12.345).y(12.345).build();

        when(placeRepository.findById(placeId)).thenReturn(Optional.ofNullable(place));

        // when
        Place result = placeService.findPlaceByPlaceId(placeId);

        // then
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertEquals("testPlace", result.getPlaceName());
        assertEquals(12.345, result.getX());
        assertEquals(12.345,result.getY());
    }
}