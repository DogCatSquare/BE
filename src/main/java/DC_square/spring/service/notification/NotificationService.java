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
        // 1. FCM 메시지 전송
        firebaseMessageService.sendMessage(requestDto);

        // 2. User 객체 조회
        var user = userRepository.findById(requestDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID: " + requestDto.getId()));

        // 3. Notification 객체 생성
        Notification notification = Notification.builder()
                .user(user) // ✅ User 객체 전달
                .notificationType(requestDto.getNotificationType())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        // 4. 저장
        notificationRepository.save(notification);
    }

    // 오버로드 메서드 - CommentService에서 사용하는 형태
    public void sendNotificationAndSave(
            NotificationType notificationType,
            User targetUser,
            String title,
            String content) {

        // FcmMessageRequestDto 생성
        FcmMessageRequestDto requestDto = FcmMessageRequestDto.builder()
                .id(targetUser.getId())
                .notificationType(notificationType)
                .title(title)
                .content(content)
                .build();

        // 기존 메서드 호출
        sendNotificationAndSave(requestDto);
    }
}