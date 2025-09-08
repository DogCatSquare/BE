package DC_square.spring.web.controller.notification;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.dday.DdayService;
import DC_square.spring.web.dto.response.notification.ToggleAlarmResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dday")
@Tag(name = "Notification", description = "알림 관련 API")
public class DdayAlarmController {

    private final DdayService ddayService;

    public static class ToggleRequest {
        @NotNull public LocalDate startDate;
        @NotNull @Min(1) public Integer termWeeks;
        @NotNull public Boolean enabled;
    }

    @Operation(summary = "반려동물 알림 토글 API", description = "startDate: 시작날짜 "
        + "\n\ntermWeeks: 주기"
        + "\n\nenabled: 주기 알람 받기 활성화/비활성화")
    @PostMapping("/{ddayId}/alarm")
    public ApiResponse<ToggleAlarmResponseDto> toggleAlarm(
            @PathVariable Long ddayId,
            @RequestParam Long userId,
            @RequestBody ToggleRequest request
    ) {
        ToggleAlarmResponseDto response = ddayService.toggleAlarm(userId, ddayId, request.startDate, request.termWeeks, request.enabled);
        return ApiResponse.onSuccess(response, "알림 설정이 변경되었습니다.");
    }
}
