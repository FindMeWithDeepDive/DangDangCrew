package findme.dangdangcrew.place.service;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.dto.PlaceResponseDto;
import findme.dangdangcrew.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final WebClient kakaoWebClient;

    @Value("${kakao_api_key}")
    private String apiKey;
    private static final String KAKAO_PLACE_SEARCH_URL = "/v2/local/search/keyword.json";

    @Transactional
    public Place findOrCreatePlace(PlaceRequestDto placeRequestDto) {
        return placeRepository.findById(placeRequestDto.id())
                .orElseGet(() -> {
                    log.info("DB에 없는 장소 ID: {} -> Kakao API에서 조회", placeRequestDto.id());
                    PlaceResponseDto placeDto = fetchPlaceFromKakao(placeRequestDto.placeName(), placeRequestDto.x(), placeRequestDto.y());
                    Place newPlace = placeDto.toEntity();
                    return placeRepository.save(newPlace);
                });
    }
    private PlaceResponseDto fetchPlaceFromKakao(String placeName, String x, String y) {
        log.info("Kakao API 호출 - 검색어: {}", placeName);

        // Kakao API 호출
        Map<String, Object> kakaoResponse = callKakaoApi(placeName, x, y);

        // 응답 데이터에서 documents 리스트 추출
        List<Map<String, Object>> documents = extractDocuments(kakaoResponse);

        // 첫 번째 장소 데이터 추출
        Map<String, Object> placeData = extractFirstPlace(documents);

        // 좌표 변환
        double[] coordinates = convertCoordinates(placeData);

    return PlaceResponseDto.fromKakaoApiResponse(placeData, coordinates[0], coordinates[1]);
}

    /**
     *  1. Kakao API 호출 메서드
     */
    private Map<String, Object> callKakaoApi(String placeName, String x, String y) {
        return kakaoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(KAKAO_PLACE_SEARCH_URL)
                        .queryParam("query", placeName)
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .queryParam("radius", 10) // 반경 10m
                        .queryParam("size", 1) // 1개 결과만 가져오기
                        .build())
                .header("Authorization", "KakaoAK " + apiKey)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    /**
     *  2. Kakao API 응답에서 'documents' 리스트 추출
     */
    private List<Map<String, Object>> extractDocuments(Map<String, Object> kakaoResponse) {
        if (kakaoResponse == null || !kakaoResponse.containsKey("documents")) {
            throw new CustomException(ErrorCode.DOCUMENTS_NOT_FOUND);
        }

        Object documentsObj = kakaoResponse.get("documents");

        if (!(documentsObj instanceof List<?> documents) || documents.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_DATA_TYPE);
        }

        return (List<Map<String, Object>>) documents;
    }

    /**
     *  3. documents 리스트에서 첫 번째 장소 데이터 추출
     */
    private Map<String, Object> extractFirstPlace(List<Map<String, Object>> documents) {
        Object firstItem = documents.get(0);

        if (!(firstItem instanceof Map<?, ?> placeData)) {
            throw new CustomException(ErrorCode.INVALID_DATA_TYPE);
        }

        return (Map<String, Object>) placeData;
    }

    /**
     * 4. 좌표 값 변환 (String → Double)
     */
    private double[] convertCoordinates(Map<String, Object> placeData) {
        String xString = (String) placeData.get("x");
        String yString = (String) placeData.get("y");

        log.info("xString: {}, yString: {}", xString, yString);

        double xValue = (xString != null && !xString.isBlank()) ? Double.parseDouble(xString.trim()) : 0.0;
        double yValue = (yString != null && !yString.isBlank()) ? Double.parseDouble(yString.trim()) : 0.0;

        return new double[]{xValue, yValue}; // x, y 좌표 배열 반환
    }

}
