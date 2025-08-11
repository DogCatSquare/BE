package DC_square.spring.service.notification;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.notification.Notification;
import DC_square.spring.domain.enums.NotificationType;
import DC_square.spring.repository.NotificationRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.request.notification.FcmMessageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FirebaseMessageService firebaseMessageService;
    private final UserRepository userRepository;

    public void sendNotificationAndSave(FcmMessageRequestDto requestDto) {
        firebaseMessageService.sendMessage(requestDto);

        var user = userRepository.findById(requestDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID: " + requestDto.getId()));

        Notification notification = Notification.builder()
                .user(user)
                .notificationType(requestDto.getNotificationType())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        notificationRepository.save(notification);
    }

    public void sendNotificationAndSave(
            NotificationType notificationType,
            User targetUser,
            String title,
            String content) {

        FcmMessageRequestDto requestDto = FcmMessageRequestDto.builder()
                .id(targetUser.getId())
                .notificationType(notificationType)
                .title(title)
                .content(content)
                .build();

        sendNotificationAndSave(requestDto);
    }
}