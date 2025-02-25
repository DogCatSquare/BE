package DC_square.spring.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewResponseDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private List<String> imageUrls;
    private Long placeId;
    private Long walkId;
}
