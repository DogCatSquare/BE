package DC_square.spring.domain.entity.notification;

import DC_square.spring.domain.entity.Dday;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String boardName;

    @Column(nullable = true)
    private String postTitle;

    @Column
    private String commenterName;

    @Column
    private String commentContent;

    @Column(nullable = false)
    private String ddayName;

    @Column(nullable = false)
    private Integer daysRemaining;

    @Embedded
    private NotificationContent content;

    @Embedded
    private RelatedUrl url;

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private Boolean read = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Notification(User user, NotificationType notificationType, String content, String url,
                        String boardName, String postTitle, String commenterName, String commentContent,
                        String ddayName, Integer daysRemaining) {
        this.user = user;
        this.notificationType = notificationType;
        this.content = new NotificationContent(content);
        this.url = new RelatedUrl(url);
        this.read = false;
        this.boardName = boardName;
        this.postTitle = postTitle;
        this.commenterName = commenterName;
        this.commentContent = commentContent;
        this.ddayName = ddayName;
        this.daysRemaining = daysRemaining;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 댓글 관련 알림
    public static Notification createCommentNotification(User user, String content, String url,
                                                         String boardName, String postTitle,
                                                         String commenterName, String commentContent) {
        return Notification.builder()
                .user(user)
                .notificationType(NotificationType.COMMENT)
                .content(new NotificationContent(content))
                .url(new RelatedUrl(url))
                .boardName(boardName)
                .postTitle(postTitle)
                .commenterName(commenterName)
                .commentContent(commentContent)
                .build();
    }

    // 디데이 관련 알림
    public static Notification createDdayNotification(User user, String content, String url,
                                                      String ddayName, Integer daysRemaining) {
        return Notification.builder()
                .user(user)
                .notificationType(NotificationType.DDAY)
                .content(new NotificationContent(content))
                .url(new RelatedUrl(url))
                .ddayName(ddayName)
                .daysRemaining(daysRemaining)
                .build();
    }

    public static int calculateDaysRemaining(Dday dday) {
        if (dday == null || dday.getDay() == null) {
            return 0;
        }
        return (int) LocalDate.now().until(dday.getDay()).getDays();
    }

    public String getContent() {
        return content.getContent();
    }

    public String getUrl() {
        return url.getUrl();
    }

    public void read(){
        read = true;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}