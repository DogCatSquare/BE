package DC_square.spring.repository;

import DC_square.spring.domain.entity.Quiz;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

  // 랜덤으로 퀴즈 1개 조회 (홈화면용)
  @Query(value = "SELECT * FROM quiz ORDER BY RAND() LIMIT 1", nativeQuery = true)
  Optional<Quiz> findRandom();
}
