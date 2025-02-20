package DC_square.spring.service.WalkService;
import DC_square.spring.web.dto.response.walk.GeocodingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ReverseGeocodingService {

    @Value("${GOOGLE_MAPS_API_KEY}")
    private String apiKey;

    @Autowired
    private final RestTemplate restTemplate;

    public ReverseGeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAddressFromCoordinates(double latitude, double longitude) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json";

        String requestUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("latlng", latitude + "," + longitude)
                .queryParam("key", apiKey)
                .toUriString();

        GeocodingResponse response = restTemplate.getForObject(requestUrl, GeocodingResponse.class);

        if (response != null && !response.getResults().isEmpty()) {
            return response.getResults().get(0).getFormattedAddress();
        }

        return "주소를 찾을 수 없습니다.";
    }
}
