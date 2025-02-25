package DC_square.spring.web.dto.request.walk;

import DC_square.spring.domain.entity.Coordinate;
import DC_square.spring.domain.enums.Difficulty;
import DC_square.spring.domain.enums.Special;
import DC_square.spring.web.dto.response.walk.WalkDetailResponseDto;
import DC_square.spring.web.dto.response.walk.WalkResponseDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class WalkCreateRequestDto {
    private String title;
    private String description;
    private Integer time;
    private Double distance;
    private Difficulty difficulty;
    private List<WalkResponseDto.SpecialDto> special;
    private List<Coordinate> coordinates;
}