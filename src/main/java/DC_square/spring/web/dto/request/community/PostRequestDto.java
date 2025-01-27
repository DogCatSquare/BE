package DC_square.spring.web.dto.request.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostRequestDto {
    private Long boardId;

    @NotBlank(message = "게시글 제목은 필수입니다")
    @Size(min = 2, max = 15,  message = "게시글 제목은 최소 2자~15자입니다.")
    private String title;

    @NotBlank(message = "게시글 내용은 필수입니다")
    @Size(max = 300,  message = "게시글 내용은 300자를 초과할 수 없습니다.")
    private String content;
    private String video_URL;
    private LocalDateTime created_at;
}
