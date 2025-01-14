package DC_square.spring.web.controller;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.UserService;
import DC_square.spring.web.dto.request.UserRequestDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /**
     * 유저 생성 API
     */
    @Operation(summary = "유저 생성 API", description = "유저 생성하는 API입니다. 이메일, 닉네임, 비밀번호를 입력해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @PostMapping("/users/register")
    public ApiResponse<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto user = userService.createUser(userRequestDto);
        return ApiResponse.onSuccess(user);
    }

    /***
     * 유저 조회 API
     */
    @Operation(summary = "유저 조회 API", description = "유저 조회하는 API입니다.")
    @GetMapping("/users")
    public ApiResponse<UserResponseDto> getUserById(@RequestParam("userid") Long userId) {
        UserResponseDto user = userService.findUserById(userId);
        return ApiResponse.onSuccess(user);
    }
}
