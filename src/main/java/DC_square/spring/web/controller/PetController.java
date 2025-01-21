package DC_square.spring.web.controller;


import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.UserService;
import DC_square.spring.web.dto.request.user.PetRegistrationDto;
import DC_square.spring.web.dto.request.user.PetsAddRequestDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}