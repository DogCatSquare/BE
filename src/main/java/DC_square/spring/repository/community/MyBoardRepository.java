package DC_square.spring.repository.community;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.community.Board;
import DC_square.spring.domain.entity.community.MyBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MyBoardRepository extends JpaRepository<MyBoard, Long> {
    List<MyBoard> findByUserOrderByIdAsc(User user);
    /**
     특정 User가 등록한 MyBoard 목록을 조회합니다.
     MyBoard의 id 기준 오름차순(OrderByIdAsc)으로 정렬하여 반환합니다.
     즉, 가장 먼저 등록한 게시판이 먼저 나오고, 최근에 등록한 것이 나중에 나오도록 정렬됩니다.
     */

    boolean existsByUserAndBoard(User user,Board board);
    long countByUser(User user);
}
