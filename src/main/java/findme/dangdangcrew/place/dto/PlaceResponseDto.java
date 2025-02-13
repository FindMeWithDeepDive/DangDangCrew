package findme.dangdangcrew.place.dto;

import findme.dangdangcrew.place.domain.Place;

import java.util.List;
import java.util.Map;

public record PlaceResponseDto(
        String id,
        String placeName,
        String addressName,
        String roadAddressName,
        String categoryGroupCode,
        String categoryGroupName,
        String phone,
        String placeUrl,
        double x,     // 경도 (longitude)
        double y      // 위도 (latitude)
) {
    // Kakao API 응답을 기반으로 PlaceDto 생성 (JSON 데이터 → DTO)
    public static PlaceResponseDto fromKakaoApiResponse(Map<String, Object> kakaoResponse, double xValue,double yValue) {

        return new PlaceResponseDto(
                (String) kakaoResponse.get("id"),
                (String) kakaoResponse.get("place_name"),
                (String) kakaoResponse.get("address_name"),
                (String) kakaoResponse.get("road_address_name"),
                (String) kakaoResponse.get("category_group_code"),
                (String) kakaoResponse.get("category_group_name"),
                (String) kakaoResponse.get("phone"),
                (String) kakaoResponse.get("place_url"),
                xValue,
                yValue
        );
    }

    // DTO → Entity 변환 (DB 저장용)
    public Place toEntity() {
        return Place.builder()
                .id(this.id)
                .placeName(this.placeName)
                .addressName(this.addressName)
                .roadAddressName(this.roadAddressName)
                .categoryGroupCode(this.categoryGroupCode)
                .categoryGroupName(this.categoryGroupName)
                .phone(this.phone)
                .placeUrl(this.placeUrl)
                .x(this.x)
                .y(this.y)
                .build();
    }
}
