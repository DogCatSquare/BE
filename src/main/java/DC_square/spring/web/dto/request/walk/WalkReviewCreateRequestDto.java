package DC_square.spring.web.dto.request.walk;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class WalkReviewCreateRequestDto {
    private String content;
}
