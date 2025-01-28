package DC_square.spring.web.controller;


import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.UserService;
import DC_square.spring.web.dto.request.user.PetRegistrationDto;
import DC_square.spring.web.dto.response.PetResponseDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Pet", description = "반려동물 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class PetController {

    private final UserService userService;

    @Operation(summary = "반려동물 추가 API", description = "사용자의 반려동물을 추가하는 API입니다. 한 마리 를  추가할 수 있습니다.")
    @PostMapping(value = "/{userId}/pets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponseDto> addPets(
            @PathVariable Long userId,
            @RequestPart("request") PetRegistrationDto request,
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
    ) {
        return ApiResponse.onSuccess(userService.addPet(userId, request, petImage));
    }

    @Operation(summary = "반려동물 전체 목록 조회 API", description = "사용자의 모든 반려동물 정보를 조회하는 API입니다.")
    @GetMapping("/{userId}/allpets")
    public ApiResponse<List<PetResponseDto>> getUserPets(@PathVariable Long userId){
        return ApiResponse.onSuccess(userService.getPets(userId));
    }

    @Operation(summary = "반려동물 상세 조회 API", description = "특정 반려동물의 상세 정보를 조회하는 API입니다.")
    @GetMapping("/{userId}/pets/{petId}")
    public ApiResponse<PetResponseDto> getPetDetail(
            @PathVariable Long userId,
            @PathVariable Long petId) {
        return ApiResponse.onSuccess(userService.getPetDetail(userId, petId));
    }


    @Operation(summary = "반려동물 삭제  API", description = "반려동물 정보를 삭제하는 API입니다.")
    @DeleteMapping("/{userId}/delpet/{petId}")
    public ApiResponse<String> deletePet(@PathVariable Long userId, @PathVariable Long petId){
        userService.deletePet(userId,petId);
        return ApiResponse.onSuccess("반려동물이 성공적으로 삭제되었습니다");
    }

    @Operation(summary = "반려동물 수정 API", description = "반려동물 정보를 수정하는 API입니다.")
    @PutMapping(value = "/{userId}/modifypet/{petId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<PetResponseDto> modifyPet(
            @PathVariable Long userId,
            @PathVariable Long petId,
            @RequestPart("request") PetRegistrationDto request,
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
    ) {
        return ApiResponse.onSuccess(userService.modifyPet(userId, petId, request, petImage));
    }
}