package DC_square.spring.web.controller.notification;

import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.security.CustomUserDetails;
import DC_square.spring.service.UserService;
import DC_square.spring.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final UserService userService;
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @Tag(name = "SSE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2000", description = "SSE 연결 성공"),
            @ApiResponse(responseCode = "5000", description = "SSE 연결 실패")
    })
    @Operation(summary = "SSE 연결")
    @GetMapping(value = "/api/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(
            @RequestParam("token") String token,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId
    ) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        Long userId = userService.findUserIdByEmail(userEmail);
        return notificationService.subscribe(userId, lastEventId);
    }
}