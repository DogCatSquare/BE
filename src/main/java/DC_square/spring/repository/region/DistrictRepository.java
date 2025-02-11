package DC_square.spring.repository.region;

import DC_square.spring.domain.entity.region.City;
import DC_square.spring.domain.entity.region.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    Optional<District> findByNameAndCity(String name, City city);
}