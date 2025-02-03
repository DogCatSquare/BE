package DC_square.spring.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import DC_square.spring.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String phoneNumber;
    private String regionId;
    private String profileImageUrl;
    private String token;

    // 토큰 (회원가입)
    public static UserResponseDto from(User user, String token) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .regionId(user.getRegionId())
                .profileImageUrl(user.getProfileImageUrl())
                .token(token)
                .build();
    }
    
    // 일반 사용자 조회
        public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .regionId(user.getRegionId())
                .profileImageUrl(user.getProfileImageUrl())
                .build();

    }
}