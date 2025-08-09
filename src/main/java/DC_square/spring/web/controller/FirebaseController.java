package DC_square.spring.web.controller;

import DC_square.spring.service.FirebaseMessageService;
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

    @Autowired
    public FirebaseController(FirebaseMessageService firebaseMessageService) {
        this.firebaseMessageService = firebaseMessageService;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@Valid @RequestBody FcmMessageRequestDto requestDto) {
        String response = firebaseMessageService.sendMessage(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registerToken")
    public ResponseEntity<String> registerToken(@RequestBody FcmTokenRequestDto tokenRequestDto) {
        firebaseMessageService.saveFcmToken(tokenRequestDto.getId(), tokenRequestDto.getFcmToken());
        return ResponseEntity.ok("FCM token registered successfully");
    }
}

