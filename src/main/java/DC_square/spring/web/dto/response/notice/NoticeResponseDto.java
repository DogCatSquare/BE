package DC_square.spring.web.dto.response.notice;

import DC_square.spring.domain.entity.notice.Notice;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResponseDto {

  private Long id;
  private String title;
  private String content;
  private LocalDateTime createdAt;

  public static NoticeResponseDto from(Notice notice) {
    return NoticeResponseDto.builder()
        .id(notice.getId())
        .title(notice.getTitle())
        .content(notice.getContent())
        .createdAt(notice.getCreatedAt())
        .build();
  }

}
