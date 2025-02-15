package DC_square.spring.repository.community;

import DC_square.spring.domain.entity.community.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByBoardNameContainingIgnoreCase(String boardName);  // 부분검색 + 대소문자 구분 없이 검색
    //📌 ContainingIgnoreCase란?
    //findByBoardNameContainingIgnoreCase(String boardName)
    //부분 검색(Containing): 검색어가 포함된 모든 데이터를 찾음 ("고양이" 입력 -> "고양이게시판1", "고양이게시판2", "내 고양이"까지 모두 검색 가능)
    //대소문자 무시(IgnoreCase): 고양이 / 고양이게시판 / Goyang 등 대소문자 구분 없이 검색

    List<Board> findByBoardNameIn(List<String> boardNames);
}
