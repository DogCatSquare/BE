package DC_square.spring.web.dto.request.community;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class BoardRequestDto {

    @Size(max = 8)
    private String title;

    @Size(max = 20)
    private String content;

    private List<String> keywords;

    private LocalDateTime createdAt;

}
