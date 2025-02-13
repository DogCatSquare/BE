package DC_square.spring.repository.community;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.community.Board;
import DC_square.spring.domain.entity.community.MyBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MyBoardRepository extends JpaRepository<MyBoard, Long> {
    List<MyBoard> findByUser(User user);
    boolean existsByUserAndBoard(User user,Board board);
    long countByUser(User user);
}
