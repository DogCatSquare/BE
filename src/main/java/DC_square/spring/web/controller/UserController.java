package DC_square.spring.web.controller;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.UserService;
import DC_square.spring.web.dto.request.LoginRequestDto;
import DC_square.spring.web.dto.request.UserRequestDto;
import DC_square.spring.web.dto.request.user.UserRegistrationRequestDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import DC_square.spring.web.dto.response.user.UserInqueryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입 API", description = "새로운 유저를 생성하는 API입니다.")
    @PostMapping(value = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<UserResponseDto> register(
            @RequestPart("request") UserRegistrationRequestDto request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "petImage", required = false) MultipartFile petImage
    ){
        return ApiResponse.onSuccess(userService.createUser(request, profileImage,petImage));
    }

    @Operation(summary = "로그인 API", description = "이메일과 비밀번호로 로그인하는 API입니다.")
    @PostMapping("/login")
    public ApiResponse<UserResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        UserResponseDto response = userService.login(request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "이메일 중복 확인 API", description = "이메일 중복을 확인하는 API입니다.")
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = userService.checkEmailDuplicate(email);
        return ApiResponse.onSuccess(isDuplicate);
    }

    @Operation(summary = "닉네임 중복 확인 API", description = "닉네임 중복을 확인하는 API입니다.")
    @GetMapping("/check-nickname")
    public ApiResponse<Boolean> checkNicknameDuplicate(@RequestParam String nickname) {
        boolean isDuplicate = userService.checkNicknameDuplicate(nickname);
        return ApiResponse.onSuccess(isDuplicate);
    }

    @Operation(summary = "유저 조회 API", description = "유저 조회하는 API입니다.")
    @GetMapping("/{userId}/users-inquiry")
    public ApiResponse<UserInqueryResponseDto> getUserById(@PathVariable  Long userId) {
        UserInqueryResponseDto userInfo = userService.getUserInfo(userId);
        return ApiResponse.onSuccess(userInfo);
    }
}
