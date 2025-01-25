package DC_square.spring.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;
    private static final long VERIFICATION_CODE_EXPIRE_TIME = 300L; // 5분

    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(
                getKey(email),
                code,
                Duration.ofSeconds(VERIFICATION_CODE_EXPIRE_TIME)
        );
    }

    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(getKey(email));
    }

    public void removeVerificationCode(String email) {
        redisTemplate.delete(getKey(email));
    }

    private String getKey(String email) {
        return "EmailVerification:" + email;
    }
}