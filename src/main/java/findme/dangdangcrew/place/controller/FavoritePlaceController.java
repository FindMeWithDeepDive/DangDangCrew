package findme.dangdangcrew.place.controller;


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
    public ResponseEntity<String> registerFavoritePlace(@RequestParam("userId") Long userId,
                                                @RequestBody PlaceRequestDto placeRequestDto){
        String message = favoritePlaceService.registerFavoritePlace(userId, placeRequestDto);
        return ResponseEntity.ok(userId + "번 유저가 " + message + " 장소를 즐겨찾기 하였습니다.");
    }

    @GetMapping
    public ResponseEntity<List<FavoritePlaceResponseDto>> getAllFavoritePlace(@RequestParam("userId") Long userId){
        List<FavoritePlaceResponseDto> favoritePlaceResponseDtos = favoritePlaceService.getAllFavoritePlace(userId);
        return ResponseEntity.ok(favoritePlaceResponseDtos);
    }
}
