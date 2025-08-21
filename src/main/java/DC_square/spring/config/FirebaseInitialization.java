package DC_square.spring.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FirebaseInitialization {
    @PostConstruct
    public void initialize() {
        try {
            String firebaseConfig = System.getenv("FIREBASE_CONFIG");

            if (firebaseConfig == null || firebaseConfig.isEmpty()) {
                throw new RuntimeException("환경변수 FIREBASE_CONFIG가 설정되어 있지 않습니다.");
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8))
            );

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("FirebaseApp 초기화 완료");
            } else {
                System.out.println("FirebaseApp 이미 초기화되어 있음");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Firebase 초기화 중 오류 발생: " + e.getMessage());
        }
    }
}
