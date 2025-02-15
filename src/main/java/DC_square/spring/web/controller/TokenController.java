package DC_square.spring.web.controller;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.config.jwt.RefreshTokenRedisRepository;
import DC_square.spring.web.dto.response.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Token", description = "토큰 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Operation(summary = "토큰 재발급 API", description = "Refresh Token을 통해 새로운 Access Token을 발급받습니다.")
    @PostMapping("/refresh")
    public ApiResponse<TokenDto> refresh(@RequestHeader("RefreshToken") String refreshToken) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 새로운 토큰 발급
        String userEmail = jwtTokenProvider.getUserEmail(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(userEmail);

        return ApiResponse.onSuccess(TokenDto.builder()
                .accessToken(newAccessToken) // 새로 발급 받은 access token
                .refreshToken(refreshToken) //기존 refresh token
                .build());
    }
}