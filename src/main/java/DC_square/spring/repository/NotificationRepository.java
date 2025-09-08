package DC_square.spring.repository;

import DC_square.spring.domain.entity.notification.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<UserNotification, Long> {
}