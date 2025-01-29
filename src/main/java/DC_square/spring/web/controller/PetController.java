package DC_square.spring.web.controller;


import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.service.UserService;
import DC_square.spring.web.dto.request.user.PetRegistrationDto;
import DC_square.spring.web.dto.response.PetResponseDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Pet", description = "반려동물 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets")
public class PetController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Operation(summary = "반려동물 추가 API", description = "사용자의 반려동물을 추가하는 API입니다. 한 마리를 추가할 수 있습니다.", security = @SecurityRequirement(name = "Authorization"))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponseDto> addPets(
            HttpServletRequest request,
            @RequestPart("request") PetRegistrationDto petRequest,
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
    ) {
        User user = getUserFromToken(request);
        return ApiResponse.onSuccess(userService.addPet(user.getId(), petRequest, petImage));
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

    // 토큰에서 사용자 정보를 가져오는 공통 메서드
    private User getUserFromToken(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String userEmail = jwtTokenProvider.getUserEmail(token);
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}