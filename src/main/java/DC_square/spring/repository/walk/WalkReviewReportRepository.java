package DC_square.spring.repository.walk;

import DC_square.spring.domain.entity.walk.WalkReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkReviewReportRepository extends JpaRepository<WalkReviewReport, Long> {

  // 이미 신고했는지 확인 (중복 신고 방지)
  boolean existsByReportingUserIdAndWalkReviewId(Long reportingUserId, Long walkReviewId);
}
