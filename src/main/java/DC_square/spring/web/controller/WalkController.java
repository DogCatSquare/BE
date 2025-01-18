package DC_square.spring.web.controller;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.WalkService.WalkService;
import DC_square.spring.web.dto.request.WalkRequestDto;
import DC_square.spring.web.dto.response.WalkResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WalkController {

    private final WalkService walkService;

    @Operation(summary = "산책로 목록 조회 api", description = "산책로 목록을 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @Parameters({
            @Parameter(name = "userId", description = "조회하는 유저의 아이디"),
            @Parameter(name = "latitude", description = "현재 위치의 위도"),
            @Parameter(name = "longitude", description = "현재 위치의 경도")
    })
    @PostMapping("/walks")
    public ApiResponse<WalkResponseDto> viewWalkList(
            @RequestBody WalkRequestDto walkRequestDto
    ) {
        WalkResponseDto walkResponseDto = walkService.viewWalkList(walkRequestDto);
        return ApiResponse.onSuccess(walkResponseDto);
    }
}