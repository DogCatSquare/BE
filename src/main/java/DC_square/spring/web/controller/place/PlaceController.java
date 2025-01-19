package DC_square.spring.web.controller.place;

import DC_square.spring.domain.entity.place.PlaceDetail;
import DC_square.spring.web.dto.request.place.PlaceCreateRequestDTO;
import DC_square.spring.web.dto.request.place.PlaceRequestDTO;
import DC_square.spring.web.dto.response.UserResponseDto;
import DC_square.spring.web.dto.response.place.PlaceDetailResponseDTO;
import DC_square.spring.web.dto.response.place.PlaceResponseDTO;
import DC_square.spring.domain.enums.PlaceCategory;
import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.place.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions/{regionId}/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

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
            @PathVariable("placeId") Integer placeId
    ) {
        PlaceDetailResponseDTO place = placeService.findPlaceDetailById(placeId);
        return ApiResponse.onSuccess(place);
    }
}