package DC_square.spring.service;

import DC_square.spring.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendVerificationEmail(String email) throws MessagingException {
        String verificationCode = generateVerificationCode();

        // 이메일 내용 생성
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail);
        helper.setTo(email);
        helper.setSubject("댕냥스퀘어 이메일 인증 코드입니다.");
        helper.setText(createEmailContent(verificationCode), true);

        // Redis에 인증 코드 저장
        redisUtil.saveVerificationCode(email, verificationCode);

        // 이메일 발송
        mailSender.send(message);
    }

    public boolean verifyEmail(String email, String code) {
        String savedCode = redisUtil.getVerificationCode(email);
        if (savedCode == null) {
            return false;
        }

        boolean isVerified = savedCode.equals(code);
        if (isVerified) {
            redisUtil.removeVerificationCode(email);
        }
        return isVerified;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private String createEmailContent(String code) {
        return String.format("""
    <div style='text-align: center; font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #FFFFFF;'>
        <img src='https://s3후에.com/logo.png' alt='댕냥스퀘어 로고' style='width: 100px; margin-bottom: 20px;'>
        <div style='background-color: #FFEED9; padding: 30px; border-radius: 10px; position: relative; margin: 20px;'>
            <!-- 각 모서리의 이미지들 -->
            <img src='https://s3후에.com/bone.png' alt='뼈다귀' style='position: absolute; top: 10px; left: 10px; width: 40px;'>
            <img src='https://s3후에/dog.png' alt='강아지' style='position: absolute; top: 10px; right: 10px; width: 40px;'>
            <img src='https://s3후에/cat.png' alt='고양이' style='position: absolute; bottom: 10px; left: 10px; width: 40px;'>
            <img src='https://s3후에/yarn.png' alt='실타래' style='position: absolute; bottom: 10px; right: 10px; width: 40px;'>

            <!-- 메인 컨텐츠 -->
            <h2 style='color: #333; margin-bottom: 20px; font-size: 24px;'>댕냥스퀘어 인증 코드</h2>
            <p style='color: #666; margin-bottom: 20px;'>아래의 인증 코드를 입력해주세요:</p>
            <h1 style='color: #5879F2; font-size: 32px; letter-spacing: 5px; margin: 30px 0; font-weight: bold;'>%s</h1>
            <p style='color: #666; font-size: 14px;'>이 코드는 5분간 유효합니다.</p>
        </div>
    </div>
    """, code);
    }
}