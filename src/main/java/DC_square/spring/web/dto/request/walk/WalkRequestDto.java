package DC_square.spring.web.dto.request.walk;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WalkRequestDto {
    private Long userId;
    private Double latitude;
    private Double longitude;
    private Double radius;
}
