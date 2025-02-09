package DC_square.spring.web.dto.request.place;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PlaceReviewCreateRequestDTO {
    private String content;
    private List<String> placeReviewImageUrl;
    private LocalDateTime createdAt;
}
