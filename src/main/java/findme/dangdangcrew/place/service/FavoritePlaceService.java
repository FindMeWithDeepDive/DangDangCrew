package findme.dangdangcrew.place.service;

import findme.dangdangcrew.global.dto.PagingResponseDto;
import findme.dangdangcrew.global.exception.CustomException;
import findme.dangdangcrew.global.exception.ErrorCode;
import findme.dangdangcrew.place.domain.FavoritePlace;
import findme.dangdangcrew.place.domain.Place;
import findme.dangdangcrew.place.dto.FavoritePlaceResponseDto;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.repository.FavoritePlaceRepository;
import findme.dangdangcrew.user.entity.User;
import findme.dangdangcrew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class FavoritePlaceService {
    private final PlaceService placeService;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final UserService userService;

    @Transactional
    public FavoritePlaceResponseDto registerFavoritePlace(PlaceRequestDto placeRequestDto){
        User user = userService.getCurrentUser();
        Place place = placeService.findOrCreatePlace(placeRequestDto);

        if(favoritePlaceRepository.existsByUserAndPlace(user,place)){
            log.info("이미 즐겨찾기한 장소입니다. - userId: {}, placeId: {}", user.getId(), place.getId());
            throw new CustomException(ErrorCode.ALREADY_FAVORITE_PLACE);
        }
        LocalDateTime createdAt = LocalDateTime.now();

        FavoritePlace favoritePlace = FavoritePlace.builder()
                .user(user)
                .place(place)
                .createdAt(createdAt)
                .build();

        favoritePlaceRepository.save(favoritePlace);
        log.info("즐겨찾기 등록 완료 - userId: {}, placeId: {}", user.getId(), place.getId());

        return FavoritePlaceResponseDto.of(favoritePlace.getId(), user.getId(),favoritePlace.getPlace());
    }

    public PagingResponseDto getAllFavoritePlace(int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page > 0 ? --page : page, 10, sort);
        User user = userService.getCurrentUser();
        Page<FavoritePlace> favoritePlaces = favoritePlaceRepository.findAllByUser(user, pageable);

        long total = favoritePlaces.getTotalElements();
        long pages = favoritePlaces.getTotalPages();

        List<FavoritePlaceResponseDto> favoritePlaceResponseDtos = favoritePlaces.getContent().stream()
                .map(favoritePlace -> FavoritePlaceResponseDto.of(favoritePlace.getId(), user.getId(),favoritePlace.getPlace()))
                .collect(Collectors.toList());

        return PagingResponseDto.of(pages, total, favoritePlaceResponseDtos);
    }
}
