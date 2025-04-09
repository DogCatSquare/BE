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

    @Query("SELECT COUNT(n) > 0 FROM Notification n " +
            "WHERE n.user = :user " +
            "AND n.notificationType = :type " +
            "AND n.postTitle = :title " +
            "AND n.daysRemaining = :daysRemaining " +
            "AND n.createdAt BETWEEN :start AND :end")
    boolean existsDdayNotification(
            @Param("user") User user,
            @Param("type") NotificationType type,
            @Param("title") String title,
            @Param("daysRemaining") Integer daysRemaining,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
