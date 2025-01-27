package DC_square.spring.web.dto.request.community;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "게시판 이름은 필수입니다")
    @Size(max = 8,  message = "게시판 이름은 8자를 초과할 수 없습니다")
    private String boardName;

    @NotBlank(message = "게시글 설명은 필수입니다")
    @Size(max = 20,  message = "게시판 설명은 20자를 초과할 수 없습니다")
    private String content;

    @Size(max = 3, message = "키워드는 최대 3개까지만 입력 가능합니다.")
    private List<String> keywords;

    private LocalDateTime createdAt;

}
