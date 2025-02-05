package DC_square.spring.service.place;

import DC_square.spring.domain.entity.Region;
import DC_square.spring.domain.entity.place.PlaceDetail;
import DC_square.spring.domain.entity.place.PlaceImage;
import DC_square.spring.repository.RegionRepository;
import DC_square.spring.repository.place.PlaceDetailRepository;
import DC_square.spring.web.dto.request.place.PlaceCreateRequestDTO;
import DC_square.spring.web.dto.request.place.PlaceRequestDTO;
import DC_square.spring.web.dto.response.place.PlaceDetailResponseDTO;
import DC_square.spring.web.dto.response.place.PlaceResponseDTO;
import DC_square.spring.domain.entity.place.Place;
import DC_square.spring.domain.enums.PlaceCategory;
import DC_square.spring.repository.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;
    private final PlaceDetailRepository placeDetailRepository;
    private final GooglePlacesService googlePlacesService;

    // 장소 검색 (메인)
    public List<PlaceResponseDTO> findPlaces(Long regionId, PlaceRequestDTO request) {
        List<PlaceDetailResponseDTO> places = searchNearbyPlaces(
                request.getLatitude(),
                request.getLongitude(),
                request.getKeyword(),
                regionId
        );

        return places.stream()
                .map(place -> PlaceResponseDTO.builder()
                        .id(place.getId())
                        .name(place.getName())
                        .address(place.getAddress())
                        .category(PlaceCategory.valueOf(place.getCategory()))
                        .phoneNumber(place.getPhoneNumber())
                        .distance(place.getDistance())
                        .open(place.getOpen())
                        .regionId(regionId)
                        .imgUrl(place.getImageUrls() != null && !place.getImageUrls().isEmpty()
                                ? place.getImageUrls().get(0)  // 첫 번째 이미지 URL 사용
                                : null)
                        .build())
                .collect(Collectors.toList());
    }

    // 주변 장소 검색
    public List<PlaceDetailResponseDTO> searchNearbyPlaces(Double latitude, Double longitude,String keyword, Long regionId) {
        Region region = regionRepository.getReferenceById(regionId);
        String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? "veterinary" : keyword;
        Map<String, Object> searchResults = googlePlacesService.searchPlacesByKeyword(latitude, longitude, searchKeyword);
        List<Map<String, Object>> results = (List<Map<String, Object>>) searchResults.get("results");
        //System.out.println("Search Results: " + searchResults); //로그
        //System.out.println("Number of results before filtering: " + (results != null ? results.size() : 0)); //로그

        if (results == null) {
            return new ArrayList<>();
        }

        PlaceRequestDTO userLocation = new PlaceRequestDTO();
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);

        return results.stream()
                .filter(this::isPetRelatedPlace)
                .map(result -> saveAndConvertToDTO(result, region, userLocation))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 장소 상세 정보 조회
    public PlaceDetailResponseDTO findPlaceDetailById(Long placeId) {
        PlaceDetail placeDetail = placeDetailRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));

        return convertToDetailDTO(placeDetail.getPlace(), null);
    }

    // 반려동물 관련 장소 필터링
//    private boolean isPetRelatedPlace(Map<String, Object> placeData) {
//        String name = ((String) placeData.get("name")).toLowerCase();
//        List<String> types = (List<String>) placeData.get("types");
//
//        List<String> petKeywords = Arrays.asList(
//                "pet", "동물", "애견", "강아지", "고양이", "veterinary",
//                "동물병원", "펫", "애견카페", "애견호텔", "펫샵", "pet hotel", "pet cafe", "animal",
//                "dog", "cat"
//        );
//
//        boolean hasKeywordInName = petKeywords.stream()
//                .anyMatch(keyword -> name.contains(keyword.toLowerCase()));
//
//        boolean hasKeywordInTypes = types != null && types.stream()
//                .anyMatch(type -> type.contains("veterinary") || type.contains("pet"));
//
//        return hasKeywordInName || hasKeywordInTypes;
//    }
    private boolean isPetRelatedPlace(Map<String, Object> placeData) {
        String name = ((String) placeData.get("name")).toLowerCase();

        // 반려동물 관련 키워드 리스트
        List<String> petKeywords = Arrays.asList(
                "애견", "펫", "동물", "쥬", "pet", "animal", "zoo",
                "강아지", "고양이", "독","도그", "dog", "cat", "멍",  "댕", "냥",
                "공원", "park",
                "veterinary"
        );

        // 이름에 반려동물 관련 키워드가 포함되어 있는지 확인
        return petKeywords.stream()
                .anyMatch(keyword -> name.contains(keyword));
    }

    // Google Place 데이터 저장 및 변환
    private PlaceDetailResponseDTO saveAndConvertToDTO(Map<String, Object> placeData, Region region, PlaceRequestDTO userLocation) {
        String googlePlaceId = (String) placeData.get("place_id");

        Place existingPlace = placeRepository.findByGooglePlaceId(googlePlaceId).orElse(null);
        if (existingPlace != null) {
            return convertToDetailDTO(existingPlace, userLocation);
        }

        Map<String, Object> details = googlePlacesService.getPlaceDetails(googlePlaceId);
        Map<String, Object> detailResult = (Map<String, Object>) details.get("result");

        Place newPlace = createPlaceFromGoogleData(placeData, detailResult, region);
        Place savedPlace = placeRepository.save(newPlace);

        PlaceDetail placeDetail = PlaceDetail.builder()
                .place(savedPlace)
                .businessHours(detailResult.containsKey("opening_hours") ?
                        ((Map<String, Object>)detailResult.get("opening_hours")).get("weekday_text").toString() : null)
                .homepageUrl((String) detailResult.get("website"))
                .description(detailResult.containsKey("editorial_summary") ?
                        ((Map<String, Object>)detailResult.get("editorial_summary")).get("overview").toString() : null)
                .facilities(new ArrayList<>())
                .build();
        placeDetailRepository.save(placeDetail);

        saveGooglePlaceImages(detailResult, savedPlace);

        return convertToDetailDTO(savedPlace, userLocation);
    }

    // Place 엔티티 생성
    private Place createPlaceFromGoogleData(Map<String, Object> placeData, Map<String, Object> details, Region region) {
        Map<String, Object> geometry = (Map<String, Object>) placeData.get("geometry");
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");
        Map<String, Object> openingHours = (Map<String, Object>) placeData.get("opening_hours");

        String phoneNumber = details != null ? (String) details.get("formatted_phone_number") : null;

        return Place.builder()
                .name((String) placeData.get("name"))
                .address((String) placeData.get("formatted_address"))
                .category(determinePlaceCategory(placeData))
                .phoneNumber(phoneNumber)
                .latitude((Double) location.get("lat"))
                .longitude((Double) location.get("lng"))
                .region(region)
                .googlePlaceId((String) placeData.get("place_id"))
                .open(openingHours != null ? (Boolean) openingHours.get("open_now") : null)
                .images(new ArrayList<>())
                .build();
    }

    // 이미지 정보 저장
    private void saveGooglePlaceImages(Map<String, Object> placeData, Place place) {
        List<Map<String, Object>> photos = (List<Map<String, Object>>) placeData.get("photos");
        if (photos == null) return;

        for (Map<String, Object> photo : photos) {
            PlaceImage placeImage = PlaceImage.builder()
                    .photoReference((String) photo.get("photo_reference"))
                    .width((Integer) photo.get("width"))
                    .height((Integer) photo.get("height"))
                    .place(place)
                    .build();
            place.getImages().add(placeImage);
        }
    }

    // 카테고리 결정
    private PlaceCategory determinePlaceCategory(Map<String, Object> placeData) {
        String name = ((String) placeData.get("name")).toLowerCase();

        // 동물병원 카테고리
        if (name.contains("병원") || name.contains("수의") || name.contains("의료")
                || name.contains("메디컬") || name.contains("hospital")) {
            return PlaceCategory.HOSPITAL;
        }

        // 호텔 카테고리
        if (name.contains("호텔") ||name.contains("hotel")) {
            return PlaceCategory.HOTEL;
        }

        // 카페 카테고리
        if (name.contains("카페") || name.contains("cafe")) {
            return PlaceCategory.CAFE;
        }

        // 공원 카테고리
        if (name.contains("공원") || name.contains("운동장") || name.contains("파크")|| name.contains("park")) {
            return PlaceCategory.PARK;
        }

        // 위 카테고리에 해당하지 않는 경우
        return PlaceCategory.ETC;
    }

    // DTO 변환
    private PlaceDetailResponseDTO convertToDetailDTO(Place place, PlaceRequestDTO userLocation) {
        Double distance = null;
        if (userLocation != null) {
            distance = calculateDistance(
                    userLocation.getLatitude(),
                    userLocation.getLongitude(),
                    place.getLatitude(),
                    place.getLongitude()
            );
        }

        return PlaceDetailResponseDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .category(place.getCategory().name())
                .phoneNumber(place.getPhoneNumber())
                .open(place.getOpen())
                .longitude(place.getLongitude())
                .latitude(place.getLatitude())
                .distance(distance)
                .imageUrls(place.getImages().stream()
                        .map(image -> googlePlacesService.getPhotoUrl(image.getPhotoReference(), image.getWidth()))
                        .collect(Collectors.toList()))
                .build();
    }

    // 거리 계산
    private Double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 반지름 (km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // km 단위로 변환
    }

    // 장소 생성 (관리자용)
    public Long createPlace(PlaceCreateRequestDTO request, Long regionId) {
        Region region = regionRepository.getReferenceById(regionId);

        Place place = Place.builder()
                .name(request.getName())
                .address(request.getAddress())
                .category(request.getCategory())
                .phoneNumber(request.getPhoneNumber())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .open(request.getOpen())
                .region(region)
                .build();

        Place savedPlace = placeRepository.save(place);

        PlaceDetail placeDetail = PlaceDetail.builder()
                .place(savedPlace)
                .businessHours(request.getBusinessHours())
                .homepageUrl(request.getHomepageUrl())
                .description(request.getDescription())
                .facilities(request.getFacilities())
                .build();

        placeDetailRepository.save(placeDetail);

        return savedPlace.getId();
    }
}