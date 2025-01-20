package DC_square.spring.web.controller.walk;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.WalkService.WalkService;
import DC_square.spring.web.dto.request.walk.WalkRequestDto;
import DC_square.spring.web.dto.request.walk.WalkCreateRequestDto;
import DC_square.spring.web.dto.response.walk.WalkCreateResponseDto;
import DC_square.spring.web.dto.response.walk.WalkDetailResponseDto;
import DC_square.spring.web.dto.response.walk.WalkResponseDto;
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

    @Operation(summary = "산책로 세부 정보 조회 API", description = "특정 산책로의 세부 정보를 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON404", description = "산책로를 찾을 수 없음")
    })
    @Parameter(name = "walkId", description = "조회할 산책로의 ID")
    @GetMapping("/walks/{walkId}")
    public ApiResponse<WalkDetailResponseDto> getWalkDetails(
            @PathVariable Long walkId
    ) {
        WalkDetailResponseDto walkDetailResponseDto = walkService.getWalkDetails(walkId);
        return ApiResponse.onSuccess(walkDetailResponseDto);
    }

    @Operation(summary = "산책로 등록 API", description = "새로운 산책로를 등록하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON201", description = "산책로 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "입력 데이터가 유효하지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON401", description = "로그인 필요"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON500", description = "서버 오류")
    })
    @PostMapping("/walks/create/{userId}")
    public ApiResponse<WalkCreateResponseDto> createWalk(
            @PathVariable Long userId,
            @RequestBody WalkCreateRequestDto walkCreateRequestDto
    ) {
        WalkCreateResponseDto walkCreateResponseDto = walkService.createWalk(walkCreateRequestDto, userId);
        return ApiResponse.onSuccess(walkCreateResponseDto);
    }
}