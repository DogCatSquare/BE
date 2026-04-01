package DC_square.spring.repository.walk;

import DC_square.spring.domain.entity.walk.WalkReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkReportRepository extends JpaRepository<WalkReport, Long> {

  // 이미 신고했는지 확인 (중복 신고 방지)
  boolean existsByReportingUserIdAndWalkId(Long reportingUserId, Long walkId);
}
