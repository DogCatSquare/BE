package DC_square.spring.config.aop;

import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.community.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect // 이 클래스가 AOP 역할(메서드 실행을 중간에 가로채는 역할)을 한다고 Spring에 알려줌
@Component // Spring이 이 클래스를 Bean으로 등록해서 자동으로 동작하게 함
@RequiredArgsConstructor
public class AccountStopAspect {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  // @Before : 대상 메서드 실행 전에 끼어든다는 뜻
  // "@annotation(...)" : 이 어노테이션이 붙은 메서드를 대상으로 한다는 뜻
  @Before("@annotation(DC_square.spring.annotation.CheckAccountStop)")
  // @CheckAccountStop 이 붙은 메서드가 실행되기 전에 끼어들어라
  public void checkAccountStop(JoinPoint joinPoint) {
    // JoinPoint : @CheckAccountStop이 붙은 메서드의 정보를 담고 있는 객체
    // 예) createPost(List images, PostRequestDto dto, Long userId) 라면
    //     파라미터 이름 ["images", "dto", "userId"] 와 실제 값을 꺼낼 수 있음

    // 실행된 메서드의 파라미터 이름 목록을 가져옴
    // 예) ["images", "postRequestDto", "userId"]
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] paramNames = signature.getParameterNames();

    // 실제로 전달된 파라미터 값 목록을 가져옴
    // 예) [MultipartFile객체, PostRequestDto객체, 13L]
    Object[] args = joinPoint.getArgs();

    User user = null;

    // 파라미터 이름과 값을 순서대로 순회하면서 token 또는 userId를 찾음
    for (int i = 0; i < paramNames.length; i++) {
      // token 파라미터가 있는 경우 (WalkService, WalkReviewService, PlaceReviewService)
      // "instanceof String token" : args[i]가 String이면 token 변수에 담음 (Java 16+ 문법)
      if ("token".equals(paramNames[i]) && args[i] instanceof String token) {
        if (!jwtTokenProvider.validateToken(token)) {
          return; // 유효하지 않은 토큰이면 체크 중단
        }
        String userEmail = jwtTokenProvider.getUserEmail(token);
        user = userRepository.findByEmail(userEmail).orElse(null);
        break; // 유저를 찾았으니 더 이상 반복할 필요 없음
      }

      // userId 파라미터가 있는 경우 (PostService, CommentService, PostLikeService)
      // "instanceof Long userId" : args[i]가 Long이면 userId 변수에 담음
      if (("userId".equals(paramNames[i]) || "currentUserId".equals(paramNames[i]))
          && args[i] instanceof Long userId) {
        user = userRepository.findById(userId).orElse(null);
        break; // 유저를 찾았으니 더 이상 반복할 필요 없음
      }
    }

    // token도 userId도 없거나 유저가 없으면 체크하지 않고 그냥 통과
    if (user == null) {
      return;
    }

    // accountStopEndAt이 null이 아니고 현재 시각보다 미래이면 → 아직 정지 중
    if (user.getAccountStopEndAt() != null &&
        user.getAccountStopEndAt().isAfter(LocalDateTime.now())) {
      // 예외를 던지면 원래 메서드는 실행되지 않고 여기서 멈춤
      throw new RuntimeException("계정이 정지된 상태입니다. 정지 해제일: " + user.getAccountStopEndAt());
    }

    // 정지 상태가 아니면 아무것도 안하고 끝 -> 원래 메서드가 정상 실행됨
  }
}
