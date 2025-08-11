package DC_square.spring.service.notification;

import DC_square.spring.service.UserService;
import DC_square.spring.web.dto.request.notification.FcmMessageRequestDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessageService {

    private final UserService userService;

    public FirebaseMessageService(UserService userService) {
        this.userService = userService;
    }

    public String sendMessage(FcmMessageRequestDto requestDto) {
        Long userId = requestDto.getId();

        String userFirebaseToken = userService.findFirebaseTokenById(userId);
        if (userFirebaseToken == null || userFirebaseToken.isEmpty()) {
            return "User's FCM token is missing or invalid";
        }

        Message message = Message.builder()
                .setToken(userFirebaseToken)
                .setNotification(
                        Notification.builder()
                                .setTitle(requestDto.getTitle())
                                .setBody(requestDto.getContent())
                                .build()
                )
                .putData("title", requestDto.getTitle())
                .putData("content", requestDto.getContent())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            return "Message sent successfully: " + response;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Failed to send message: " + e.getMessage();
        }
    }

    public void saveFcmToken(Long id, String fcmToken) {
        userService.saveFirebaseToken(id, fcmToken);
    }
}
