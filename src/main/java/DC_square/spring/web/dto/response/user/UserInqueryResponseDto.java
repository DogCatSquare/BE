package DC_square.spring.web.dto.response.user;

import DC_square.spring.domain.entity.Region;
import DC_square.spring.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInqueryResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String phoneNumber;
    private String doName;
    private String si;
    private String gu;
    private Boolean adAgree;
    private String firstPetBreed; // 첫 번째 반려동물 품종
    private String profileImageUrl;

    public static UserInqueryResponseDto fromUser(User user, Region region, String firstPetBreed) {
        return UserInqueryResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .doName(region.getDoName())
                .si(region.getSi())
                .gu(region.getGu())
                .adAgree(user.getAdAgree())
                .firstPetBreed(firstPetBreed)
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
