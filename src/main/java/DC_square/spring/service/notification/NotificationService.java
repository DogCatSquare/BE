package DC_square.spring.service.notification;

import DC_square.spring.domain.entity.notification.Notification;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.notification.NotificationContent;
import DC_square.spring.domain.entity.notification.RelatedUrl;
import DC_square.spring.domain.enums.NotificationType;
import DC_square.spring.repository.NotificationRepository.EmitterRepository;
import DC_square.spring.repository.NotificationRepository.EmitterRepositoryImpl;
import DC_square.spring.repository.NotificationRepository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import DC_square.spring.mapper.NotificationMapper;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private final NotificationRepository notificationRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + memberId + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    public void send(User user, NotificationType notificationType, String content, String url,
                     String boardName, String commentContent, String commenterName, String postTitle) {
        Notification notification = notificationRepository.save(
                createCommentNotification(user, notificationType, content, url,
                        boardName, commentContent, commenterName, postTitle));

        String memberId = String.valueOf(user.getId());

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, NotificationMapper.NotificationtoResponseNotificationDto(notification));
                }
        );
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new RuntimeException("알림 전송 중 오류가 발생했습니다.");
        }
    }

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

}