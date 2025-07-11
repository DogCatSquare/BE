package DC_square.spring.repository.NotificationRepository;

import DC_square.spring.domain.entity.notification.Notification;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.NotificationType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUser(User user);
}
