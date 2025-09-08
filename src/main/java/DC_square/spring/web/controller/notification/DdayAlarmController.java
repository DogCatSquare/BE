package DC_square.spring.web.controller.notification;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.dday.DdayService;
import DC_square.spring.web.dto.response.notification.ToggleAlarmResponseDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dday")
public class DdayAlarmController {

    private final DdayService ddayService;

    public static class ToggleRequest {
        @NotNull public LocalDate startDate;
        @NotNull @Min(1) public Integer termWeeks;
        @NotNull public Boolean enabled;
    }

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
