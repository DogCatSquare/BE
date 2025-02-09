package DC_square.spring.service.place;

import DC_square.spring.config.GoogleMapsConfig;
import DC_square.spring.domain.enums.PlaceCategory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GooglePlacesService {
    private final GoogleMapsConfig googleMapsConfig;
    private final RestTemplate restTemplate;
    private static final String PLACES_API_BASE_URL = "https://maps.googleapis.com/maps/api/place";
    private static final String TRANSLATE_API_BASE_URL = "https://translation.googleapis.com/language/translate/v2";

    private String translateToEnglish(String text) {
        String requestUrl = UriComponentsBuilder
                .fromHttpUrl(TRANSLATE_API_BASE_URL)
                .queryParam("key", googleMapsConfig.getTranslateApiKey()) // 번역 API 키 사용
                .queryParam("q", text)
                .queryParam("source", "ko") // 한국어 → 영어 변환
                .queryParam("target", "en")
                .queryParam("format", "text")
                .build()
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(requestUrl, Map.class);

        if (response != null && response.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            var translations = (java.util.List<Map<String, Object>>) data.get("translations");

            if (!translations.isEmpty()) {
                return (String) translations.get(0).get("translatedText"); // 번역된 텍스트 반환
            }
        }
        return text; // 번역 실패 시 원래 텍스트 반환
    }

    public Map<String, Object> searchPlacesByKeyword(double latitude, double longitude, String keyword) {
        String translatedKeyword = translateToEnglish(keyword);
        System.out.println("🔍 변환된 키워드: " + translatedKeyword);
        String encodedKeyword = URLEncoder.encode(translatedKeyword, StandardCharsets.UTF_8);

        String url = UriComponentsBuilder
                .fromHttpUrl(PLACES_API_BASE_URL + "/textsearch/json")
                .queryParam("query", encodedKeyword)
                .queryParam("location", latitude + "," + longitude)
                .queryParam("radius", 3000)
                .queryParam("language", "ko")
                .queryParam("key", googleMapsConfig.getApiKey())
                .build()
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }

//    // 카테고리로 장소 검색
//    public Map<String, Object> searchPlacesByCategory(double latitude, double longitude, PlaceCategory category) {
//        String keyword = getCategoryKeyword(category);
//        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
//
//        String url = UriComponentsBuilder
//                .fromHttpUrl(PLACES_API_BASE_URL + "/textsearch/json")
//                .queryParam("query", encodedKeyword)
//                .queryParam("location", latitude + "," + longitude)
//                .queryParam("radius", 3000)
//                .queryParam("language", "ko")
//                .queryParam("key", googleMapsConfig.getApiKey())
//                .build()
//                .toUriString();
//
//        return restTemplate.getForObject(url, Map.class);
//    }
//
//    private String getCategoryKeyword(PlaceCategory category) {
//        return switch (category) {
//            case HOSPITAL -> "동물병원 veterinary";
//            case HOTEL -> "애견호텔 반려동물호텔";
//            case PARK -> "애견운동장 반려동물공원";
//            case CAFE -> "애견카페 펫카페";
//            case ETC -> "펫샵 동물용품";
//        };
//    }

    public Map<String, Object> getPlaceDetails(String placeId) {
        String url = UriComponentsBuilder
                .fromHttpUrl(PLACES_API_BASE_URL + "/details/json")
                .queryParam("place_id", placeId)
                .queryParam("fields", "name,formatted_phone_number,formatted_address,opening_hours,website,photos,editorial_summary,geometry")
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