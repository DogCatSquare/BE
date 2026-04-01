package DC_square.spring.service.walk;

import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.walk.Walk;
import DC_square.spring.domain.entity.walk.WalkReport;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.walk.WalkReportRepository;
import DC_square.spring.repository.walk.WalkRepository;
import DC_square.spring.web.dto.request.walk.WalkReportRequestDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkReportService {

  private final WalkReportRepository walkReportRepository;
  private final WalkRepository walkRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  private static final int STOP_TRIGGER_COUNT = 3; // 몇 회 신고 시 정지할지
  private static final int STOP_DAYS = 1; // 정지 기간 (일) [테스트로 1일로 설정, 원래 7일]

  @Transactional
  public void reportWalk(Long walkId, WalkReportRequestDto requestDto, String token) {

    if (token == null || !jwtTokenProvider.validateToken(token)) {
      throw new IllegalArgumentException("잘못된 토큰입니다.");
    }

    String userEmail = jwtTokenProvider.getUserEmail(token);
    User reportingUser = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    Walk walk = walkRepository.findById(walkId)
        .orElseThrow(() -> new RuntimeException("산책로를 찾을 수 없습니다."));

    // 자기 자신 신고 방지
    if (walk.getCreatedBy().getId().equals(reportingUser.getId())) {
      throw new RuntimeException("자신의 산책로는 신고할 수 없습니다.");
    }

    // 중복 신고 방지
    if (walkReportRepository.existsByReportingUserIdAndWalkId(reportingUser.getId(), walkId)) {
      throw new RuntimeException("이미 신고한 산책로입니다.");
    }

    User reportedUser = walk.getCreatedBy();

    WalkReport report = WalkReport.builder()
        .walk(walk)
        .reportingUser(reportingUser)
        .reportedUser(reportedUser)
        .reportType(requestDto.getReportType())
        .otherReason(requestDto.getOtherReason())
        .build();

    walkReportRepository.save(report);

    // 신고받은 횟수 증가 및 정지 처리
    reportedUser.setReceivedReportCount(reportedUser.getReceivedReportCount() + 1);
    if (reportedUser.getReceivedReportCount() >= STOP_TRIGGER_COUNT) {
      reportedUser.setAccountStopEndAt(LocalDateTime.now().plusDays(STOP_DAYS));
    }
    userRepository.save(reportedUser);
  }
}