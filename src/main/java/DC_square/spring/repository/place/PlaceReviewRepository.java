package DC_square.spring.repository.place;

import DC_square.spring.domain.entity.place.PlaceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
    List<PlaceReview> findAllByPlaceId(Long placeId);

}
