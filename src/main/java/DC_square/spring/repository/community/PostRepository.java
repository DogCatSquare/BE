package DC_square.spring.repository.community;

import DC_square.spring.domain.entity.community.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

  // 유저가 존재하는 게시글만 조회 (탈퇴 유저 게시글 제외)
  @Query("SELECT p FROM Post p JOIN p.user u JOIN p.board b")
  List<Post> findAllWithValidUsers();

  @Query("SELECT p FROM Post p JOIN p.user u JOIN p.board b WHERE p.board.id = :boardId")
  List<Post> findByBoardIdWithValidUsers(@Param("boardId") Long boardId);

  @Query("SELECT p FROM Post p JOIN p.user u WHERE p.user.id = :userId")
  List<Post> findByUserIdWithValidUsers(@Param("userId") Long userId);

  @Query("SELECT p FROM Post p JOIN p.user u JOIN p.board b WHERE p.id = :postId")
  Optional<Post> findByIdWithValidUser(@Param("postId") Long postId);

  @Query("SELECT p FROM Post p JOIN p.user u WHERE p.likeCount >= :likeCount ORDER BY p.likeCount DESC")
  List<Post> findPopularPostsWithValidUsers(@Param("likeCount") int likeCount);
}
