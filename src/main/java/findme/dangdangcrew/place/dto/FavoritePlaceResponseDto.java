package findme.dangdangcrew.place.dto;

import findme.dangdangcrew.place.domain.Place;
import lombok.Builder;

@Builder
public record FavoritePlaceResponseDto(
        Long userId,
        String placeId,
        String placeName,
        String addressName
) {
    public static FavoritePlaceResponseDto of(Place place, Long userId){
        return FavoritePlaceResponseDto.builder()
                .userId(userId)
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .addressName(place.getAddressName())
                .build();
    }

}
