package DC_square.spring.mapper;

import DC_square.spring.domain.entity.notification.Notification;
import DC_square.spring.web.dto.response.notification.NotificationResponseDto;

public class NotificationMapper {

    public static NotificationResponseDto NotificationtoResponseNotificationDto(Notification notification) {
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
                .isRead(notification.getRead())
                .notificationType(notification.getNotificationType())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
