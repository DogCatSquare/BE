package DC_square.spring.domain.entity.walk;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.ReportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "walk_review_report")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkReviewReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "walk_review_id", nullable = false)
  private WalkReview walkReview; // 신고된 후기

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporting_user_id", nullable = false)
  private User reportingUser; // 신고한 유저

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reported_user_id", nullable = false)
  private User reportedUser; // 신고 받은 유저

  @Column(name = "report_type", nullable = false)
  private ReportType reportType;

  @Column(name = "other_reason", length = 50)
  private String otherReason; // 기타 사유

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
  }

}
