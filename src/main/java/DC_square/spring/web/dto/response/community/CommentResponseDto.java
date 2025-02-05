package DC_square.spring.web.dto.response.community;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private String name;
    private String animal_type;
    private String profileImage_URL;
    private LocalDateTime created_at;
    private List<CommentResponseDto> replies;
}
