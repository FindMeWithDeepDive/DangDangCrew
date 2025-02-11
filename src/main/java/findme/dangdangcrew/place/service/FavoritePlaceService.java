package findme.dangdangcrew.place.service;

import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.place.domain.FavoritePlace;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.FavoritePlaceResponseDto;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.repository.FavoritePlaceRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class FavoritePlaceService {
    private final PlaceService placeService;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public String registerFavoritePlace(Long userId, PlaceRequestDto placeRequestDto){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        Place place = placeService.findOrCreatePlace(placeRequestDto);

        if(favoritePlaceRepository.existsByUserAndPlace(user,place)){
            log.info("이미 즐겨찾기한 장소입니다. - userId: {}, placeId: {}", user.getId(), place.getId());
            throw new CustomException(ErrorCode.ALREADY_FAVORITE_PLACE);
        }

        FavoritePlace favoritePlace = FavoritePlace.builder()
                .user(user)
                .place(place)
                .build();

        favoritePlaceRepository.save(favoritePlace);
        log.info("즐겨찾기 등록 완료 - userId: {}, placeId: {}", userId, place.getId());

        return place.getPlaceName();
    }

    public List<FavoritePlaceResponseDto> getAllFavoritePlace(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        List<FavoritePlace> favoritePlaces = favoritePlaceRepository.findAllWithPlaceByUser(user);

        return favoritePlaces.stream()
                .map(favoritePlace -> FavoritePlaceResponseDto.of(favoritePlace.getPlace(),user.getId()))
                .collect(Collectors.toList());
    }
}
