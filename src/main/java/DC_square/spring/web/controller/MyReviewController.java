package DC_square.spring.web.controller;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.service.MyReviewService;
import DC_square.spring.web.dto.response.ReviewResponseDTO;
import DC_square.spring.web.dto.response.place.PlacePageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MyReview", description = "마이 리뷰 모아보기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/myReviews")
public class MyReviewController {

  private final MyReviewService myReviewService;
  private final JwtTokenProvider jwtTokenProvider;

  @Operation(summary = "마이 리뷰 모아보기 API")
  @GetMapping
  public ApiResponse<PlacePageResponseDTO<ReviewResponseDTO>> getMyReviews(
      HttpServletRequest request,
      @RequestParam(defaultValue = "0") int page
  ) {
    String token = jwtTokenProvider.resolveToken(request);
    PlacePageResponseDTO<ReviewResponseDTO> reviews = myReviewService.getMyReviews(token, page, 10);
    return ApiResponse.onSuccess(reviews);
  }

  @Operation(summary = "마이 리뷰 삭제 API", description = "type: place(장소 리뷰) 또는 walk(산책로 리뷰)")
  @DeleteMapping("/{reviewId}")
  public ApiResponse<Void> deleteMyReview(
      HttpServletRequest request,
      @PathVariable Long reviewId,
      @RequestParam String type
  ) {
    String token = jwtTokenProvider.resolveToken(request);
    myReviewService.deleteReview(token, reviewId, type);
    return ApiResponse.onSuccess(null);
  }

}
