package DC_square.spring.service.notification;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.notification.UserNotification;
import DC_square.spring.domain.enums.NotificationType;
import DC_square.spring.repository.NotificationRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.request.notification.FcmMessageRequestDto;
import DC_square.spring.web.dto.request.notification.FcmTokenRequestDto;
import DC_square.spring.web.dto.response.notification.NotificationDeliveryResponseDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * FCM 발송 + 알람 이력(Notification) 저장
 * - 예약 큐(DdayAlarmReservation)는 별도 테이블에서 관리
 * - 해당 서비스는 알람 이력을 저장하는거임(Notification)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 유저의 디바이스 FCM 토큰을 저장/갱신한다.
     * - 앱에서 getToken() 으로 받은 값을 전달
     *
     * @param userId
     * @param fcmToken
     */
    @Transactional
    public void registerFcmToken(Long userId, String fcmToken) {
        if (userId == null) {
            throw new IllegalArgumentException("userId가 필요합니다.");
        }
        if (fcmToken == null || fcmToken.isBlank()) {
            throw new IllegalArgumentException("유효한 fcmToken이 필요합니다.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저: " + userId));

        user.setFcmToken(fcmToken); // JPA 더티체킹으로 저장
        log.info("FCM 토큰 등록/갱신 완료. userId={}", userId);
    }

    /**
     * 푸시 전송 + 알림 이력 저장
     * @param notificationType
     * @param targetUser
     * @param title
     * @param content
     * @return
     */
    @Transactional
    public NotificationDeliveryResponseDto sendNotificationAndSave(
            NotificationType notificationType,
            User targetUser,
            String title,
            String content
    ) {
        boolean attempted = false;
        boolean success = false;
        String messageId = null;
        String error = null;

        // 1) FCM 발송 (토큰 없으면 스킵)
        String token = targetUser.getFcmToken();
        if (token == null || token.isBlank()) {
            log.warn("해당 유저로부터 FCM 토큰이 존재하지 않습니다. userId={}. 푸시 발송은 건너뛰고 NotificationRepository에 알람 기록 저장합니다.", targetUser.getId());
        } else {
            //토큰 존재
            attempted = true; // <-- 전송을 '시도'함
            try {
                Message message = Message.builder()
                        .setToken(token)
                        .setNotification(Notification.builder().setTitle(title).setBody(content).build())
                        .build();
                String response = FirebaseMessaging.getInstance().send(message); // FCM에 알림 전송
                success = true;
                messageId = response; // FCM messageId
                log.info("FCM에 알림 전송. userId={}, response={}",targetUser.getId(), response);
            } catch (Exception e) {
                success = false;
                error = e.getMessage();
                log.error("FCM send failed. userId={}, error={}", targetUser.getId(), e.getMessage(), e);
            }
        }

        // 2) 알림 이력 저장 (NotificationRepository)
        UserNotification userNotification = UserNotification.builder()
                .user(targetUser)
                .notificationType(notificationType)
                .title(title)
                .content(content)
                .build();
        notificationRepository.save(userNotification);

        return NotificationDeliveryResponseDto.builder()
                .userId(targetUser.getId())
                .notificationType(notificationType)
                .title(title)
                .content(content)
                .fcmAttempted(attempted)
                .fcmSuccess(success)
                .fcmResponseId(messageId)
                .errorMessage(error)
                .notificationId(userNotification.getId())
                .createdAt(userNotification.getCreatedAt())
                .build();

    }

}