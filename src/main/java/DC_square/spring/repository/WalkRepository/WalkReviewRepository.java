package DC_square.spring.repository.WalkRepository;

import DC_square.spring.domain.entity.walk.Walk;
import DC_square.spring.domain.entity.walk.WalkReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalkReviewRepository extends JpaRepository<WalkReview, Long> {
    List<WalkReview> findAllByWalkId(Long walkId);

    List<WalkReview> findByWalk(Walk walk);
}
