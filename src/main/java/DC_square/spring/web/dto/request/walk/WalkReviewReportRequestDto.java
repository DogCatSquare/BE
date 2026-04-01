package DC_square.spring.web.dto.request.walk;

import DC_square.spring.domain.enums.ReportType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WalkReviewReportRequestDto {

  private ReportType reportType; // 신고 유형
  private String otherReason; // 기타 사유 (reportType이 OTHER일 때만 작성)
}
