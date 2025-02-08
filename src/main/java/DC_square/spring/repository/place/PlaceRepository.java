package DC_square.spring.repository.place;

import DC_square.spring.domain.entity.place.Place;
import DC_square.spring.domain.enums.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByGooglePlaceId(String googlePlaceId);

    boolean existsByGooglePlaceId(String googlePlaceId);
}