package DC_square.spring.web.dto.response.notification;

import DC_square.spring.domain.entity.notification.Notification;
import DC_square.spring.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {
    private Long id;

    private String boardName;
    private String postTitle;
    private String commenterName;
    private String commentContent;

    private String ddayName;
    private Integer daysRemaining;

    private String content;
    private String url;
    private Boolean isRead;
    private NotificationType notificationType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public NotificationResponseDto(Long id, String boardName, String postTitle,
                                   String commenterName, String commentContent,
                                   String ddayName, Integer daysRemaining,
                                   String content, String url, Boolean isRead,
                                   NotificationType notificationType,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.boardName = boardName;
        this.postTitle = postTitle;
        this.commenterName = commenterName;
        this.commentContent = commentContent;
        this.ddayName = ddayName;
        this.daysRemaining = daysRemaining;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
        this.notificationType = notificationType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static NotificationResponseDto fromEntity(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .boardName(notification.getBoardName())
                .postTitle(notification.getPostTitle())
                .commenterName(notification.getCommenterName())
                .commentContent(notification.getCommentContent())
                .ddayName(notification.getDdayName())
                .daysRemaining(notification.getDaysRemaining())
                .content(notification.getContent())
                .url(notification.getUrl())
                .isRead(notification.getIsRead())
                .notificationType(notification.getNotificationType())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
