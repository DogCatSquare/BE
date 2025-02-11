package DC_square.spring.web.controller;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.service.MyReviewService;
import DC_square.spring.web.dto.response.ReviewResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "MyReview", description = "마이 리뷰 모아보기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/myReviews")
public class MyReviewController {
    private final MyReviewService myReviewService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "마이 리뷰 모아보기 API")
    @GetMapping
    public ApiResponse<List<ReviewResponseDTO>> getMyReviews(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        List<ReviewResponseDTO> reviews = myReviewService.getMyReviews(token);
        return ApiResponse.onSuccess(reviews);
    }

}
