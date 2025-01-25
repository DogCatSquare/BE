package DC_square.spring.web.dto.response.walk;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class WalkReviewResponseDto {
    private List<WalkReviewDto> walkReviews;

    public WalkReviewResponseDto(List<WalkReviewDto> walkReviews) {
        this.walkReviews = walkReviews;
    }

    @Getter
    @Builder
    public static class WalkReviewDto {
        private Long reviewId;
        private Long walkId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private WalkResponseDto.CreatedByDto createdBy;
    }
}
