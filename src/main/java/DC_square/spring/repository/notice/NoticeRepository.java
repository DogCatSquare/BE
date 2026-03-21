package DC_square.spring.repository.notice;

import DC_square.spring.domain.entity.notice.Notice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

  List<Notice> findAllByOrderByCreatedAtDesc();
}
