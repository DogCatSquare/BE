package DC_square.spring.repository.place;

import DC_square.spring.domain.entity.place.PlaceReview;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {
    List<PlaceReview> findAllByPlaceId(Long placeId);

    @Query("SELECT pr FROM PlaceReview pr WHERE pr.user.id = :userId")
    List<PlaceReview> findAllByUserId(@Param("userId") Long userId);
}
