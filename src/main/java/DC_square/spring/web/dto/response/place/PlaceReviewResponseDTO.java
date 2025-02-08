package DC_square.spring.web.dto.response.place;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceReviewResponseDTO {
    private Long id;
    private String content;
    //private boolean isLiked;
    private String breed;
    private String nickname;
    private String userImageUrl;
    private String createdAt;
    private Long userId;
    private List<String> placeReviewImageUrl;
    private Long placeId;
}
