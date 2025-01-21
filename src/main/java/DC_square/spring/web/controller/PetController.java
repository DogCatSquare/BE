package DC_square.spring.web.controller;


import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.UserService;
import DC_square.spring.web.dto.request.user.PetRegistrationDto;
import DC_square.spring.web.dto.request.user.PetsAddRequestDto;
import DC_square.spring.web.dto.response.PetResponseDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pet", description = "반려동물 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class PetController {

    private final UserService userService;

    @Operation(summary = "반려동물 추가 API", description = "사용자의 반려동물을 추가하는 API입니다. 한 마리 또는 여러 마리를 동시에 추가할 수 있습니다.")
    @PostMapping("/{userId}/pets")
    public ApiResponse<UserResponseDto> addPets(
            @PathVariable Long userId,
            @Valid @RequestBody PetsAddRequestDto request) {
        return ApiResponse.onSuccess(userService.addPets(userId, request));
    }

    @Operation(summary = "반려동물 목록 조회 API", description = "사용자의 모든 반려동물 정보를 조회하는 API입니다.")
    @GetMapping("/{userId}/allpets")
    public ApiResponse<List<PetResponseDto>> getUserPets(@PathVariable Long userId){
        return ApiResponse.onSuccess(userService.getPets(userId));
    }

    @Operation(summary = "반려동물 삭제  API", description = "반려동물 정보를 삭제하는 API입니다.")
    @DeleteMapping("/{userId}/delpet/{petId}")
    public ApiResponse<String> deletePet(@PathVariable Long userId, @PathVariable Long petId){
        userService.deletePet(userId,petId);
        return ApiResponse.onSuccess("반려동물이 성공적으로 삭제되었습니다");
    }

    @Operation(summary = "반려동물 수정 API", description = "반려동물 정보를 수정하는 API입니다.")
    @PutMapping("/{userId}/modifypet/{petId}")
    public ApiResponse<PetResponseDto> modifyPet(
            @PathVariable Long userId,
            @PathVariable Long petId,
            @Valid @RequestBody PetRegistrationDto request) {
        return ApiResponse.onSuccess(userService.modifyPet(userId, petId, request));
    }

}