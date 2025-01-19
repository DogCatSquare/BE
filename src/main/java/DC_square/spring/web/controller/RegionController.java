package DC_square.spring.web.controller;

import DC_square.spring.web.dto.request.RegionRequestDTO;
import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @Operation(summary = "지역 생성 API")
    @PostMapping
    public ApiResponse<Long> createRegion(@RequestBody RegionRequestDTO request) {
        regionService.creatRegion(request);
        Long regionId = regionService.creatRegion(request);
        return ApiResponse.onSuccess(regionId);
    }
}
