package DC_square.spring.repository;

import DC_square.spring.domain.entity.Region;
import DC_square.spring.domain.entity.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region,Long> {

}
