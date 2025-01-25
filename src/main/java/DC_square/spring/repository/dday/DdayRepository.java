package DC_square.spring.repository.dday;

import DC_square.spring.domain.entity.Dday;
import DC_square.spring.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DdayRepository extends JpaRepository<Dday, Long> {
    List<Dday> findAllByUser(User user);
    List<Dday> findAllByUserOrderByDayAsc(User user);
}