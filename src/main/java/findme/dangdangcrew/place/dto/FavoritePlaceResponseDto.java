package findme.dangdangcrew.place.dto;

import findme.dangdangcrew.place.domain.Place;
import lombok.Builder;

@Builder
public record FavoritePlaceResponseDto(
        Long favoritePlaceId,
        Long userId,
        String placeId,
        String placeName,
        String addressName
) {
    public static FavoritePlaceResponseDto of(Long favoritePlaceId, Long userId, Place place){
        return FavoritePlaceResponseDto.builder()
                .favoritePlaceId(favoritePlaceId)
                .userId(userId)
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .addressName(place.getAddressName())
                .build();
    }

}
