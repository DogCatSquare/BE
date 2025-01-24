package DC_square.spring.web.dto.response.walk;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class WalkDetailResponseDto {
    private Long walkId;
    private String title;
    private String description;
    private Integer time;
    private Double distance;
    private String difficulty;
    private List<WalkResponseDto.SpecialDto> special;
    private List<WalkResponseDto.CoordinateDto> startCoordinates;
    private List<WalkResponseDto.CoordinateDto> endCoordinates;
    //private List<ImageDto> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private WalkResponseDto.CreatedByDto createdBy;
}
