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