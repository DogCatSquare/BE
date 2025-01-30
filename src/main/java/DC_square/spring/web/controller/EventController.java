package DC_square.spring.web.controller;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.EventService;
import DC_square.spring.web.dto.response.EventResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Event", description = "이벤트 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "전국 반려동물 이벤트 조회 API", description = "현재 진행 중인 이벤트 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<EventResponseDto>> getEvents() {
        return ApiResponse.onSuccess(eventService.getEvents());
    }
}