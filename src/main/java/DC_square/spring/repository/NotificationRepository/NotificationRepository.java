package DC_square.spring.repository.NotificationRepository;

import DC_square.spring.domain.entity.notification.Notification;
import DC_square.spring.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUser(User user);
}
