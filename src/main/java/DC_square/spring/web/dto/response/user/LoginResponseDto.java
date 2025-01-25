package DC_square.spring.web.dto.response.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    private String token;  // JWT 토큰
    private String email;  // 사용자 이메일
    private String nickname;  // 사용자 닉네임
    private Long userId; //유저 아이디
}