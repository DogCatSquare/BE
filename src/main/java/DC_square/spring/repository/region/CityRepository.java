package DC_square.spring.repository.region;

import DC_square.spring.domain.entity.region.City;
import DC_square.spring.domain.entity.region.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByNameAndProvince(String name, Province province);
}