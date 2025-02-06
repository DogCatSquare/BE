package DC_square.spring.web.controller.place;

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
@RequestMapping("/api/regions/{regionId}/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final JwtTokenProvider jwtTokenProvider;

    // 장소 전체 조회 API
    @Operation(summary = "장소 전체 조회 API")
    @PostMapping("search")
    public ApiResponse<List<PlaceResponseDTO>> getPlaces(
            @PathVariable("regionId") Long regionId,
            @RequestBody PlaceRequestDTO request  // 사용자 현재 위치
    ) {
        List<PlaceResponseDTO> places = placeService.findPlaces(regionId, request);
        return ApiResponse.onSuccess(places);
    }

    // 장소 생성 API
    @Operation(summary = "장소 생성 API")
    @PostMapping
    public ApiResponse<Long> createPlace(
            @RequestBody PlaceCreateRequestDTO request,
            @PathVariable("regionId") Long regionId
    ) {
        Long placeId = placeService.createPlace(request, regionId);
        return ApiResponse.onSuccess(placeId);
    }

    // 장소 상세 조회 API
    @Operation(summary = "장소 상세 조회 API")
    @GetMapping("/{placeId}")
    public ApiResponse<PlaceDetailResponseDTO> getPlaceById(
            @PathVariable("placeId") Long placeId,
            HttpServletRequest request
    ) {
        String token = jwtTokenProvider.resolveToken(request);
        PlaceDetailResponseDTO place = placeService.findPlaceDetailById(placeId, token);
        return ApiResponse.onSuccess(place);
    }

    // 마이 위시 장소 조회 API
    @Operation(summary = "마이 위시 장소 조회 API")
    @GetMapping("/wishlist")
    public ApiResponse<List<PlaceResponseDTO>> getWishList(
            @PathVariable("regionId") Long regionId,
            HttpServletRequest request
    ) {
        String token = jwtTokenProvider.resolveToken(request);
        List<PlaceResponseDTO> places = placeService.findWishList(regionId, token);
        return ApiResponse.onSuccess(places);
    }
}