package DC_square.spring.web.controller.place;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.place.PlaceReviewService;
import DC_square.spring.web.dto.request.place.PlaceReviewCreateRequestDTO;
import DC_square.spring.web.dto.response.place.PlaceReviewResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places/{placeId}/reviews")
@RequiredArgsConstructor
public class PlaceReviewController {

    private final PlaceReviewService placeReviewService;

    @Operation(summary = "장소 리뷰 생성 API")
    @PostMapping
    public ApiResponse<Long> createPlaceReview(
            @RequestBody PlaceReviewCreateRequestDTO request,
            @PathVariable("placeId") Long placeId
    ) {
        Long reviewId = placeReviewService.createPlaceReview(request, placeId);
        return ApiResponse.onSuccess(reviewId);
    }

    @Operation(summary = "장소 리뷰 전체 조회 API")
    @GetMapping
    public ApiResponse<List<PlaceReviewResponseDTO>> getReviews(
            @PathVariable("placeId") Long placeId
    ) {
        List<PlaceReviewResponseDTO> reviews = placeReviewService.findPlaceReviews(placeId);
        return ApiResponse.onSuccess(reviews);
    }

    @Operation(summary = "장소 리뷰 삭제 API")
    @DeleteMapping("/{reviewId}/users/{userId}")
    public ApiResponse<Long> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        placeReviewService.deletePlaceReview(reviewId, userId);
        return ApiResponse.onSuccess(reviewId);
    }
}

