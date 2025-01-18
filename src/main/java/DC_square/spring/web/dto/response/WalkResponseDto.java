package DC_square.spring.web.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class WalkResponseDto {
    private List<WalkDto> walks;

    @Getter
    @Builder
    public static class WalkDto {
        private Long walkId;
        private String title;
        private String description;
        //private Integer reviewCount;
        private Double distance;
        private Integer time;
        //private List<ImageDto> images;
        private String difficulty;
        private List<SpecialDto> special;
        private List<CoordinateDto> coordinates;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private CreatedByDto createdBy;
    }

//    @Getter
//    @Builder
//    public static class ImageDto {
//        private Long imageId;
//        private String walkImgUrl;
//    }

    @Getter
    @Builder
    public static class SpecialDto {
        private String type;
    }

    @Getter
    @Builder
    public static class CoordinateDto {
        private Double latitude;
        private Double longitude;
        private Integer sequence;
    }

    @Getter
    @Builder
    public static class CreatedByDto {
        private String userId;
        private String nickname;
        private String breed;
        //private String imgUrl;
    }
}
