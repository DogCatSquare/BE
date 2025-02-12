package DC_square.spring.repository.community;

import DC_square.spring.domain.entity.community.Board;
import DC_square.spring.domain.entity.community.MyBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyBoardRepository extends JpaRepository<MyBoard, Long> {
    List<MyBoard> findAllByOrderByOrderIndexAsc();
    //MyBoard 엔티티의 데이터를 orderIndex 기준으로 오름차순(ASC) 정렬하여 조회
    boolean existsByBoard(Board board);
    long count();
}
