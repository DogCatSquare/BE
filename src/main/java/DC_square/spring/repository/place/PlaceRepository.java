package DC_square.spring.repository.place;

import DC_square.spring.domain.entity.place.Place;
import DC_square.spring.domain.enums.PlaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
    // Region 엔티티의 id로 조회
    @Query("SELECT p FROM Place p " +
            "WHERE (:regionId is null OR p.region.id = :regionId)")
    List<Place> findPlacesByRegionId(@Param("regionId") Long regionId);
}