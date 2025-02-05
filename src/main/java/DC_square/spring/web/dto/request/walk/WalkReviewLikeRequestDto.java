package DC_square.spring.web.dto.request.walk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalkReviewLikeRequestDto {
    private Long userId;

    private Boolean isLiked;
}
