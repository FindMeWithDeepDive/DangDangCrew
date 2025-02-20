package findme.dangdangcrew.place.controller;


import findme.dangdangcrew.global.dto.PagingResponseDto;
import findme.dangdangcrew.global.dto.ResponseDto;
import findme.dangdangcrew.place.dto.FavoritePlaceResponseDto;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.service.FavoritePlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[Favorite Place] Favorite Place API", description = "즐겨찾기 장소 관련 API")
@RestController
@RequestMapping("/api/v1/favorite-place")
@RequiredArgsConstructor
public class FavoritePlaceController {

    private final FavoritePlaceService favoritePlaceService;

    @Operation(summary = "장소 즐겨찾기 등록",description = "헤더의 토큰 기반의 유저가 장소를 즐겨찾기 합니다.")
    @PostMapping
    public ResponseEntity<ResponseDto<FavoritePlaceResponseDto>> registerFavoritePlace(@RequestBody PlaceRequestDto placeRequestDto){
        FavoritePlaceResponseDto favoritePlace = favoritePlaceService.registerFavoritePlace(placeRequestDto);
        String message =  favoritePlace.placeName() + " 장소를 즐겨찾기 하였습니다.";

        return ResponseEntity.ok(ResponseDto.of(favoritePlace, message));
    }

    @Operation(summary = "즐겨찾기 한 장소 조회", description = "헤더의 토큰 기반의 즐겨찾기한 장소를 조회합니다.")
    @GetMapping
    public ResponseEntity<ResponseDto<PagingResponseDto>> getAllFavoritePlace(@RequestParam(name = "page", required = false, defaultValue = "1") int page){
        PagingResponseDto favoritePlaceResponseDtos = favoritePlaceService.getAllFavoritePlace(page);
        return ResponseEntity.ok(ResponseDto.of(
                favoritePlaceResponseDtos,
                "유저가 즐겨찾기한 장소를 성공적으로 조회하였습니다."
                ));
    }
}
