package DC_square.spring.web.dto.request.place;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PlaceReviewCreateRequestDTO {
    private Long userId;
    private String content;
    private List<String> img_urls;
    private LocalDateTime createdAt;
}
