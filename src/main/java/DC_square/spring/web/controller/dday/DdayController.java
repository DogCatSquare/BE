package DC_square.spring.web.controller.dday;


import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.dday.DdayService;
import DC_square.spring.web.dto.request.dday.DdayRequestDto;
import DC_square.spring.web.dto.response.dday.DdayResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "D-day", description = "D-day 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class DdayController {
    private final DdayService ddayService;

    @Operation(summary = "D-day 생성 API", description = "새로운 D-day를 생성하는 API입니다.")
    @PostMapping("/{userId}")
    public ApiResponse<DdayResponseDto> createDday(
            @PathVariable Long userId,
            @Valid @RequestBody DdayRequestDto request) {
        DdayResponseDto response = ddayService.createDday(userId, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "사용자별 D-day 조회 API", description = "사용자의 모든 D-day를 조회하는 API입니다.")
    @GetMapping("/{userId}/ddays")
    public ApiResponse<List<DdayResponseDto>> getDdaysByUser(
            @PathVariable Long userId) {
        List<DdayResponseDto> response = ddayService.getDdaysByUser(userId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "D-day 삭제 API", description = "D-day를 삭제하는 API입니다.")
    @DeleteMapping("/{userId}/ddays/{ddayId}")
    public ApiResponse<Void> deleteDday(
            @PathVariable Long userId,
            @PathVariable Long ddayId) {
        ddayService.deleteDday(userId, ddayId);
        return ApiResponse.onSuccess(null);
    }
}