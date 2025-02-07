package DC_square.spring.repository.community;

import DC_square.spring.domain.entity.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByBoardId(Long boardId);
}
