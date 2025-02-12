package findme.dangdangcrew.place.controller;


import findme.dangdangcrew.global.dto.ResponseDto;
import findme.dangdangcrew.place.dto.FavoritePlaceResponseDto;
import findme.dangdangcrew.place.dto.PlaceRequestDto;
import findme.dangdangcrew.place.service.FavoritePlaceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[Favorite Place] Favorite Place API", description = "사용자가 장소에 즐겨찾기 합니다.")
@RestController
@RequestMapping("/api/v1/favorite-place")
@RequiredArgsConstructor
public class FavoritePlaceController {

    private final FavoritePlaceService favoritePlaceService;

    @PostMapping
    public ResponseEntity<ResponseDto> registerFavoritePlace(@RequestParam("userId") Long userId,
                                                @RequestBody PlaceRequestDto placeRequestDto){
        String placeName = favoritePlaceService.registerFavoritePlace(userId, placeRequestDto);
        String message = userId + "번 유저가 " + placeName + " 장소를 즐겨찾기 하였습니다.";

        return ResponseEntity.ok(ResponseDto.of(null, message));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<FavoritePlaceResponseDto>>> getAllFavoritePlace(@RequestParam("userId") Long userId){
        List<FavoritePlaceResponseDto> favoritePlaceResponseDtos = favoritePlaceService.getAllFavoritePlace(userId);
        return ResponseEntity.ok(ResponseDto.of(
                favoritePlaceResponseDtos,
                "유저가 즐겨찾기한 장소를 성공적으로 조회하였습니다."
                ));
    }
}
