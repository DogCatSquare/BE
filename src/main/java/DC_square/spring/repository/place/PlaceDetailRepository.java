package DC_square.spring.repository.place;

import DC_square.spring.domain.entity.place.Place;
import DC_square.spring.domain.entity.place.PlaceDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceDetailRepository extends JpaRepository<PlaceDetail, Integer> {
    Optional<PlaceDetail> findByPlace(Place place);
    Optional<PlaceDetail> findByPlaceId(Integer placeId);
}
