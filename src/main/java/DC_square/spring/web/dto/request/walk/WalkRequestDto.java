package DC_square.spring.web.dto.request.walk;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WalkRequestDto {
    private Double latitude;
    private Double longitude;
    @Builder.Default
    private Double radius = 100000.0;
}