package DC_square.spring.web.controller.place;

import DC_square.spring.web.dto.request.place.LocationRequestDTO;
import DC_square.spring.web.dto.request.place.PlaceCreateRequestDTO;
import DC_square.spring.web.dto.request.place.PlaceRequestDTO;
import DC_square.spring.web.dto.response.place.PlaceDetailResponseDTO;
import DC_square.spring.web.dto.response.place.PlaceResponseDTO;
import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.place.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import DC_square.spring.config.jwt.JwtTokenProvider;

import java.util.List;

@Tag(name = "Place", description = "장소 관련 API")
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final JwtTokenProvider jwtTokenProvider;

    // 장소 전체 조회 API
    @Operation(summary = "장소 전체 조회 API")
    @PostMapping("search/{cityId}")
    public ApiResponse<List<PlaceResponseDTO>> getPlaces(
            @RequestBody LocationRequestDTO location,  // 사용자 현재 위치
            @PathVariable("cityId") Long cityId,
            @RequestParam(required = false) String keyword

    ) {
        List<PlaceResponseDTO> places = placeService.findPlaces(location, cityId, keyword);
        return ApiResponse.onSuccess(places);
    }

    // 장소 생성 API
    @Operation(summary = "장소 생성 API")
    @PostMapping
    public ApiResponse<Long> createPlace(
            @RequestBody PlaceCreateRequestDTO request
    ) {
        Long placeId = placeService.createPlace(request);
        return ApiResponse.onSuccess(placeId);
    }

    // 장소 상세 조회 API
    @Operation(summary = "장소 상세 조회 API")
    @PostMapping("/{placeId}")
    public ApiResponse<PlaceDetailResponseDTO> getPlaceById(
            @PathVariable("placeId") Long placeId,
            @RequestBody LocationRequestDTO location,
            HttpServletRequest request
    ) {
        String token = jwtTokenProvider.resolveToken(request);
        PlaceDetailResponseDTO place = placeService.findPlaceDetailById(placeId, token, location);
        return ApiResponse.onSuccess(place);
    }

    // 마이 위시 장소 조회 API
    @Operation(summary = "마이 위시 장소 조회 API")
    @PostMapping("/wishlist")
    public ApiResponse<List<PlaceResponseDTO>> getWishList(
            HttpServletRequest request,
            @RequestBody LocationRequestDTO location
    ) {
        String token = jwtTokenProvider.resolveToken(request);
        List<PlaceResponseDTO> places = placeService.findWishList(token, location);
        return ApiResponse.onSuccess(places);
    }

    // 지역별 핫 플레이스 조회 API
    @Operation(summary = "지역별 핫 플레이스 조회 API", description = "위시가 많은 순서로 정렬")
    @PostMapping("/hot/{cityId}")
    public ApiResponse<List<PlaceResponseDTO>> getHotPlacesByCity(
            @PathVariable("cityId") Long cityId,
            @RequestBody LocationRequestDTO location
    ) {
        List<PlaceResponseDTO> places = placeService.findHotPlacesByCity(cityId, location);
        return ApiResponse.onSuccess(places);
    }
}