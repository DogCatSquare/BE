package DC_square.spring.service.notification;

import DC_square.spring.domain.entity.Dday;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.NotificationType;
import DC_square.spring.domain.entity.notification.Notification;
import DC_square.spring.domain.entity.notification.NotificationContent;
import DC_square.spring.domain.entity.notification.RelatedUrl;
import DC_square.spring.repository.NotificationRepository;
import DC_square.spring.mapper.NotificationMapper;
import DC_square.spring.service.notification.FirebaseMessageService;
import DC_square.spring.web.dto.request.notification.FcmMessageRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FirebaseMessageService firebaseMessageService;

    private Notification createCommentNotification(User user, NotificationType type, String content, String url,
                                                   String boardName, String commentContent, String commenterName, String postTitle) {
        return Notification.builder()
                .user(user)
                .notificationType(type)
                .content(new NotificationContent(content))
                .url(new RelatedUrl(url))
                .boardName(boardName)
                .commentContent(commentContent)
                .commenterName(commenterName)
                .postTitle(postTitle)
                .read(false)
                .build();
    }

    private Notification createDdayNotification(User user, String content, Dday dday, String url) {
        int daysRemaining = Notification.calculateDaysRemaining(dday);
        return Notification.createDdayNotification(
                user,
                content,
                url,
                dday.getTitle(),
                daysRemaining
        );
    }

    @Transactional
    public void sendCommentNotification(User user, NotificationType notificationType, String content, String url,
                                        String boardName, String commentContent, String commenterName, String postTitle) {
        Notification notification = notificationRepository.save(
                createCommentNotification(user, notificationType, content, url, boardName, commentContent, commenterName, postTitle));

        String fcmToken = user.getFcmToken();
        if (fcmToken != null && !fcmToken.isEmpty()) {
            String title = boardName;
            FcmMessageRequestDto requestDto = new FcmMessageRequestDto(
                    user.getId(),
                    notification.getNotificationType(),
                    title,
                    content,
                    fcmToken
            );
            firebaseMessageService.sendMessage(requestDto);
        }
    }

    @Transactional
    public void sendDdayNotification(User user, String content, Dday dday, String url) {
        Notification notification = notificationRepository.save(
                createDdayNotification(user, content, dday, url)
        );

        String fcmToken = user.getFcmToken();
        if (fcmToken != null && !fcmToken.isEmpty()) {
            String title = dday.getTitle();
            FcmMessageRequestDto requestDto = new FcmMessageRequestDto(
                    user.getId(),
                    notification.getNotificationType(),
                    title,
                    content,
                    fcmToken
            );
            firebaseMessageService.sendMessage(requestDto);
        }
    }
}

