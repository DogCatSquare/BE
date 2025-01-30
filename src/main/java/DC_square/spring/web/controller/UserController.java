package DC_square.spring.web.controller;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.service.UserService;
import DC_square.spring.web.dto.request.LoginRequestDto;
import DC_square.spring.web.dto.request.UserRequestDto;
import DC_square.spring.web.dto.request.user.UserRegistrationRequestDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import DC_square.spring.web.dto.response.user.LoginResponseDto;
import DC_square.spring.web.dto.response.user.UserInqueryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

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
    public ApiResponse<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = userService.login(request);
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

    @GetMapping("/users-inquiry")
    public ApiResponse<UserInqueryResponseDto> getUserInfo(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String userEmail = jwtTokenProvider.getUserEmail(token);

        User user = userRepository .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return ApiResponse.onSuccess(userService.getUserInfo(user.getId()));
    }
}
