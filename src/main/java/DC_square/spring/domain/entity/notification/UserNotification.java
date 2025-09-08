package DC_square.spring.domain.entity.notification;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 실제로 발송된 알림 이력을 저장하는 엔티티
 * - DdayAlarmReservation는 앞으로 보낼 예정인 알림을 관리
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 어떤 유저에게 보낸 알림인지

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
