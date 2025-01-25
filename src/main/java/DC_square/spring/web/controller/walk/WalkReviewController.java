package DC_square.spring.web.controller.walk;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.WalkService.WalkReviewService;
import DC_square.spring.web.dto.request.walk.WalkReviewCreateRequestDto;
import DC_square.spring.web.dto.response.walk.WalkReviewResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/walks/{walkId}/reviews")
public class WalkReviewController {

    private final WalkReviewService walkReviewService;

    @Operation(summary = "산책로 후기 등록 API", description = "특정 산책로에 대한 후기를 등록하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON201", description = "후기 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "입력 데이터가 유효하지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON401", description = "로그인 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON500", description = "서버 오류")
    })
    @Parameter(name = "walkId", description = "후기를 등록할 산책로의 ID", required = true)
    @PostMapping
    public ApiResponse<WalkReviewResponseDto> createReview(
            @PathVariable Long walkId,
            @RequestBody @Valid WalkReviewCreateRequestDto reviewCreateRequestDto
    ) {
        WalkReviewResponseDto responseDto = walkReviewService.createWalkReview(reviewCreateRequestDto, walkId);
        return ApiResponse.onSuccess(responseDto);
    }
}
