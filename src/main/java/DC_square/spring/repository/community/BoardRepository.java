package DC_square.spring.repository.community;

import DC_square.spring.domain.entity.community.Board;
import DC_square.spring.domain.enums.BoardType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {


    Board findByBoardType(BoardType boardType);

    boolean existsByBoardType(BoardType boardType);
}
