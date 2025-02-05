package DC_square.spring.web.controller.place;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.place.PlaceReviewService;
import DC_square.spring.web.dto.request.place.PlaceReviewCreateRequestDTO;
import DC_square.spring.web.dto.response.place.PlaceReviewResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "PlaceReview", description = "장소 리뷰 관련 API")
@RestController
@RequestMapping("/api/places/{placeId}/reviews")
@RequiredArgsConstructor
public class PlaceReviewController {

    private final PlaceReviewService placeReviewService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ApiResponse<Long> createPlaceReview(
            @Valid @RequestPart("request") PlaceReviewCreateRequestDTO request,
            @PathVariable("placeId") Long placeId,
            @RequestPart(value = "placeReviewImages") List<MultipartFile> images
    ) {
        return ApiResponse.onSuccess(placeReviewService.createPlaceReview(request, placeId, images));
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

