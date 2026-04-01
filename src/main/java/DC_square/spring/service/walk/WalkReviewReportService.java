package DC_square.spring.service.walk;

import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.walk.WalkReview;
import DC_square.spring.domain.entity.walk.WalkReviewReport;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.walk.WalkReviewReportRepository;
import DC_square.spring.repository.walk.WalkReviewRepository;
import DC_square.spring.web.dto.request.walk.WalkReviewReportRequestDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkReviewReportService {

  private final WalkReviewReportRepository walkReviewReportRepository;
  private final WalkReviewRepository walkReviewRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  private static final int STOP_TRIGGER_COUNT = 1; // 몇 회 신고 시 정지할지
  private static final int STOP_DAYS = 7; // 정지 기간 (일)

  @Transactional
  public void reportWalkReview(Long walkId, Long reviewId,
      WalkReviewReportRequestDto requestDto, String token) {
    if (token == null || !jwtTokenProvider.validateToken(token)) {
      throw new IllegalArgumentException("잘못된 토큰입니다.");
    }

    String userEmail = jwtTokenProvider.getUserEmail(token);

    // 현재 사용자가 신고하는 유저
    User reportingUser = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    WalkReview walkReview = walkReviewRepository.findById(reviewId)
        .orElseThrow(() -> new RuntimeException("해당 후기를 찾을 수 없습니다."));

    if (!walkReview.getWalk().getId().equals(walkId)) {
      throw new RuntimeException("해당 산책로에 존재하는 후기가 아닙니다.");
    }

    // 자기 자신 신고 방지
    if (walkReview.getUser().getId().equals(reportingUser.getId())) {
      throw new RuntimeException("자신의 후기는 신고할 수 없습니다.");
    }

    // 중복 신고 방지
    if (walkReviewReportRepository.existsByReportingUserIdAndWalkReviewId(
        reportingUser.getId(), reviewId)) {
      throw new RuntimeException("이미 신고한 후기입니다.");
    }

    User reportedUser = walkReview.getUser();

    WalkReviewReport report = WalkReviewReport.builder()
        .walkReview(walkReview)
        .reportingUser(reportingUser)
        .reportedUser(reportedUser)
        .reportType(requestDto.getReportType())
        .otherReason(requestDto.getOtherReason())
        .build();

    walkReviewReportRepository.save(report);

    // 신고 받은 횟수 증가 및 정지 처리
    reportedUser.setReceivedReportCount(reportedUser.getReceivedReportCount() + 1);
    if (reportedUser.getReceivedReportCount() >= STOP_TRIGGER_COUNT) {
      reportedUser.setAccountStopEndAt(LocalDateTime.now().plusDays(STOP_DAYS));
    }
    userRepository.save(reportedUser);
  }

}
