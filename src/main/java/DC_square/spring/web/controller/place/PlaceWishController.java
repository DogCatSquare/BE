package DC_square.spring.web.controller.place;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.service.place.PlaceWishService;
import DC_square.spring.web.dto.response.place.PlaceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PlaceWish", description = "장소 위시리스트 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist/places/{placeId}")
public class PlaceWishController {

    private final PlaceWishService placeWishService;
    private final JwtTokenProvider jwtTokenProvider;

    // 장소 위시리스트 토글 API
    @Operation(summary = "장소 위시리스트 추가 API")
    @PostMapping
    public ApiResponse<Boolean> toggleWish(
            @PathVariable("placeId") Long placeId,
            HttpServletRequest request
    ) {
        String token = jwtTokenProvider.resolveToken(request);
        Boolean isWishId = placeWishService.togglePlaceWish(token, placeId);
        return ApiResponse.onSuccess(isWishId);
    }

//    // 마이 장소 위시리스트 조회 API
//    @Operation(summary = "마이 장소 위시리스트 조회 API")
//    @GetMapping
//    public ApiResponse<List<PlaceResponseDTO>> getMyWishList(
//            HttpServletRequest request
//    ) {
//        String token = jwtTokenProvider.resolveToken(request);
//        List<PlaceResponseDTO> wishList = placeWishService.getMyWishList(token);
//        return ApiResponse.onSuccess(wishList);
//    }
}
