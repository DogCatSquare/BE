package DC_square.spring.repository.WalkRepository;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.walk.Walk;
import DC_square.spring.domain.entity.walk.WalkWish;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalkWishRepository extends JpaRepository<WalkWish, Long> {
    boolean existsByUserAndWalk(User user, Walk walk);

    Optional<WalkWish> findByUserAndWalk(User user, Walk walk);
}
