package DC_square.spring.service.place;

import DC_square.spring.config.GoogleMapsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GooglePlacesService {
    private final GoogleMapsConfig googleMapsConfig;
    private final RestTemplate restTemplate;
    private static final String PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place";
    private static final String TRANSLATE_API_BASE_URL = "https://translation.googleapis.com/language/translate/v2";

    // 근처 장소 검색
    public Map<String, Object> searchNearbyPlaces(double latitude, double longitude) {
        String url = UriComponentsBuilder
                .fromHttpUrl(PLACES_API_BASE_URL + "/nearbysearch/json")
                .queryParam("location", latitude + "," + longitude)
                .queryParam("radius", 3000)
                .queryParam("type", "veterinary_care|cafe|park|lodging|pet_store")
                .queryParam("language", "ko")
                .queryParam("maxResults", 60)
                .queryParam("key", googleMapsConfig.getApiKey())
                .build()
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }

    // 키워드로 검색
    public Map<String, Object> searchPlacesByKeyword(String keyword) {
        String url = UriComponentsBuilder
                .fromHttpUrl(PLACES_API_BASE_URL + "/textsearch/json")
                .queryParam("query", keyword)
                .queryParam("type", "veterinary_care|cafe|park|lodging|pet_store")
                .queryParam("language", "ko")
                .queryParam("key", googleMapsConfig.getApiKey())
                .build()
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }

    // 장소 상세 정보 조회
    public Map<String, Object> getPlaceDetails(String placeId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(PLACES_API_BASE_URL + "/details/json")
                .queryParam("place_id", placeId)
                .queryParam("fields", "name,formatted_phone_number,formatted_address,address_components,opening_hours,website,photos,editorial_summary,geometry")
                .queryParam("key", googleMapsConfig.getApiKey())
                .queryParam("language", "ko")
                .build()
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }

    public String getPhotoUrl(String photoReference, Integer maxWidth) {
        return UriComponentsBuilder
                .fromHttpUrl(PLACES_API_BASE_URL + "/photo")
                .queryParam("maxwidth", maxWidth)
                .queryParam("photo_reference", photoReference)
                .queryParam("key", googleMapsConfig.getApiKey())
                .build()
                .toUriString();
    }
}