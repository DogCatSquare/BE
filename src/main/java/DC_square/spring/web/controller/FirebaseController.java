package DC_square.spring.web.controller;

import DC_square.spring.service.notification.FirebaseMessageService;
import DC_square.spring.service.notification.FirebaseMessageService;
import DC_square.spring.service.notification.NotificationService;
import DC_square.spring.web.dto.request.notification.FcmMessageRequestDto;
import DC_square.spring.web.dto.request.notification.FcmTokenRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fcm")
public class FirebaseController {

    private final FirebaseMessageService firebaseMessageService;
    private final NotificationService notificationService;

    @Autowired
    public FirebaseController(FirebaseMessageService firebaseMessageService,
                              NotificationService notificationService) {
        this.firebaseMessageService = firebaseMessageService;
        this.notificationService = notificationService;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendNotification(@RequestBody @Valid FcmMessageRequestDto requestDto) {
        notificationService.sendNotificationAndSave(requestDto);
        return ResponseEntity.ok("Notification sent and saved successfully");
    }

    @PostMapping("/registerToken")
    public ResponseEntity<String> registerToken(@RequestBody FcmTokenRequestDto tokenRequestDto) {
        firebaseMessageService.saveFcmToken(tokenRequestDto.getId(), tokenRequestDto.getFcmToken());
        return ResponseEntity.ok("FCM token registered successfully");
    }
}
