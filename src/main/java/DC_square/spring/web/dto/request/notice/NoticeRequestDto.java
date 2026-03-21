package DC_square.spring.web.dto.request.notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeRequestDto {

  @NotBlank(message = "공지 제목은 필수입니다")
  private String title;

  @NotBlank(message = "공지 내용은 필수입니다")
  private String content;

}
