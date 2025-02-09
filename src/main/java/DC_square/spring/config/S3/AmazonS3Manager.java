package DC_square.spring.config.S3;

import DC_square.spring.config.AmazonConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        }catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String generateProfile(Uuid uuid) {
        return amazonConfig.getProfilePath() + '/' + uuid.getUuid();
    }

    public String generatePet(Uuid uuid) {
        return amazonConfig.getPetPath() + '/' + uuid.getUuid();
    }

    public String generateReview(Uuid uuid) {
        return amazonConfig.getReviewPath() + '/' + uuid.getUuid();
    }

    public String generateWalk(Uuid uuid) {
        return amazonConfig.getWalkPath() + '/' + uuid.getUuid();
    }

    public String generateCommunity(Uuid uuid) {
        return amazonConfig.getCommunityPath() + '/' + uuid.getUuid();
    }

    public String generatedday(Uuid uuid) {
        return amazonConfig.getDdayPath() + '/' + uuid.getUuid();
    }

    public String generateWeather(Uuid uuid) {
        return amazonConfig.getWeatherPath() + '/' + uuid.getUuid();
    }
}
