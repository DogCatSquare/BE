package DC_square.spring.service.place;

import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.place.PlaceDetail;
import DC_square.spring.domain.entity.place.PlaceImage;
import DC_square.spring.domain.entity.place.PlaceReview;
import DC_square.spring.domain.entity.region.City;
import DC_square.spring.domain.entity.region.Province;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.place.PlaceDetailRepository;
import DC_square.spring.repository.place.PlaceReviewRepository;
import DC_square.spring.repository.place.PlaceWishRepository;
import DC_square.spring.web.dto.request.place.LocationRequestDTO;
import DC_square.spring.web.dto.request.place.PlaceCreateRequestDTO;
import DC_square.spring.web.dto.response.place.PlaceDetailResponseDTO;
import DC_square.spring.web.dto.response.place.PlacePageResponseDTO;
import DC_square.spring.web.dto.response.place.PlaceResponseDTO;
import DC_square.spring.domain.entity.place.Place;
import DC_square.spring.domain.enums.PlaceCategory;
import DC_square.spring.repository.place.PlaceRepository;
import DC_square.spring.web.dto.response.place.PlaceReviewResponseDTO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceDetailRepository placeDetailRepository;
    private final PlaceReviewRepository placeReviewRepository;
    private final GooglePlacesService googlePlacesService;
    private final PlaceWishRepository placeWishRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final EntityManager em;

    // 장소 검색 (메인)
    public PlacePageResponseDTO<PlaceResponseDTO> findPlaces(
            LocationRequestDTO location,
            Long cityId,
            String keyword,
            int page,
            int size
    ) {
        List<PlaceDetailResponseDTO> places = searchNearbyPlaces(
                location.getLatitude(),
                location.getLongitude(),
                keyword,
                cityId
        );

        List<PlaceResponseDTO> responseDtos = places.stream()
                .map(place -> PlaceResponseDTO.builder()
                        .id(place.getId())
                        .name(place.getName())
                        .address(place.getAddress())
                        .category(PlaceCategory.valueOf(place.getCategory()))
                        .phoneNumber(place.getPhoneNumber())
                        .latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .distance(place.getDistance())
                        .open(place.getOpen())
                        .imgUrl(place.getImageUrls() != null && !place.getImageUrls().isEmpty()
                                ? place.getImageUrls().get(0)
                                : null)
                        .build())
                .collect(Collectors.toList());

        return PlacePageResponseDTO.of(responseDtos, page, size);
    }

    // 주변 장소 검색
    public List<PlaceDetailResponseDTO> searchNearbyPlaces(Double latitude, Double longitude, String keyword, Long cityId) {
        String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? "animal" : keyword;
        Map<String, Object> searchResults = googlePlacesService.searchPlacesByKeyword(latitude, longitude, searchKeyword);
        List<Map<String, Object>> results = (List<Map<String, Object>>) searchResults.get("results");

        if (results == null) {
            return new ArrayList<>();
        }

        LocationRequestDTO location = new LocationRequestDTO();
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return results.stream()
                .filter(this::isPetRelatedPlace)
                .map(result -> saveAndConvertToDTO(result, location, cityId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 장소 상세 정보 조회
    public PlaceDetailResponseDTO findPlaceDetailById(Long placeId, String token, LocationRequestDTO location) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        PlaceDetail placeDetail = placeDetailRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));

        return convertToDetailDTO(placeDetail.getPlace(), location, user.getId());
    }

    // 반려동물 관련 장소 필터링
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
    private PlaceDetailResponseDTO saveAndConvertToDTO(Map<String, Object> placeData, LocationRequestDTO location, Long cityId) {
        String googlePlaceId = (String) placeData.get("place_id");

        Place existingPlace = placeRepository.findByGooglePlaceId(googlePlaceId).orElse(null);
        if (existingPlace != null) {
            return convertToDetailDTO(existingPlace, location, null);
        }

        Map<String, Object> details = googlePlacesService.getPlaceDetails(googlePlaceId);
        Map<String, Object> detailResult = (Map<String, Object>) details.get("result");

        String address = (String) placeData.get("formatted_address");
        Pair<Province, City> regionInfo = extractRegionInfo(address);

        // 도시 정보가 없는 경우
        if (regionInfo == null || regionInfo.getSecond() == null) {
            String findCityQuery = "SELECT c FROM City c WHERE c.id = :cityId";
            City city = em.createQuery(findCityQuery, City.class)
                    .setParameter("cityId", cityId)
                    .getSingleResult();
            if (city != null) {
                regionInfo = Pair.of(city.getProvince(), city);
            }
        }

        Place newPlace = createPlaceFromGoogleData(placeData, detailResult, regionInfo);
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

        return convertToDetailDTO(savedPlace, location, null);
    }

    private Pair<Province, City> extractRegionInfo(String address) {
        // Province 조회
        String findProvinceQuery = "SELECT p FROM Province p";
        List<Province> provinces = em.createQuery(findProvinceQuery, Province.class)
                .getResultList();

        Province matchedProvince = provinces.stream()
                .filter(province -> address.startsWith(province.getName()))
                .findFirst()
                .orElse(null);

        if (matchedProvince == null) {
            return null;
        }

        // City 조회
        String findCityQuery = "SELECT c FROM City c WHERE c.province.id = :provinceId";
        List<City> cities = em.createQuery(findCityQuery, City.class)
                .setParameter("provinceId", matchedProvince.getId())
                .getResultList();

        City matchedCity = cities.stream()
                .filter(city -> address.contains(city.getName()))
                .findFirst()
                .orElse(null);

        return Pair.of(matchedProvince, matchedCity);
    }

    // Place 엔티티 생성
    private Place createPlaceFromGoogleData(Map<String, Object> placeData, Map<String, Object> details, Pair<Province, City> regionInfo) {
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
                .googlePlaceId((String) placeData.get("place_id"))
                .open(openingHours != null ? (Boolean) openingHours.get("open_now") : null)
                .images(new ArrayList<>())
                .province(regionInfo != null ? regionInfo.getFirst() : null)
                .city(regionInfo != null ? regionInfo.getSecond() : null)
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
    private PlaceDetailResponseDTO convertToDetailDTO(Place place, LocationRequestDTO location, Long userId) {
        Double distance = calculateDistance(
                location.getLatitude(),
                location.getLongitude(),
                place.getLatitude(),
                place.getLongitude()
        );

        int reviewCount = placeReviewRepository.countByPlaceId(place.getId());

        PlaceDetail placeDetail = placeDetailRepository.findByPlace(place)
                .orElseThrow(() -> new RuntimeException("장소 상세 정보를 찾을 수 없습니다."));

        List<PlaceReview> recentReviews = placeReviewRepository.findTop2ByPlaceOrderByCreatedAtDesc(place.getId());
        List<PlaceReviewResponseDTO> recentReviewDtos = recentReviews.stream()
                .map(review -> PlaceReviewResponseDTO.builder()
                        .id(review.getId())
                        .content(review.getContent())
                        .breed(review.getUser().getPetList().get(0).getBreed())
                        .nickname(review.getUser().getNickname())
                        .userImageUrl(review.getUser().getProfileImageUrl())
                        .createdAt(review.getCreatedAt().toString())
                        .placeReviewImageUrl(review.getPlaceReviewImageUrl())
                        .placeId(review.getPlace().getId())
                        .build())
                .collect(Collectors.toList());

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
                .isWished(userId != null && placeWishRepository.existsByPlaceIdAndUserId(place.getId(), userId))
                .businessHours(placeDetail.getBusinessHours())
                .homepageUrl(placeDetail.getHomepageUrl())
                .description(placeDetail.getDescription())
                .facilities(placeDetail.getFacilities())
                .reviewCount(reviewCount)
                .recentReviews(recentReviewDtos)
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

    // 위시리스트에서 찜한 장소만 조회하는 메서드
    public List<PlaceResponseDTO> findWishList(String token, LocationRequestDTO location) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        //사용자가 찜한 `placeId` 리스트 가져오기
        List<Long> placeIds = placeWishRepository.findAllByUserId(user.getId()).stream()
                .map(placeWish -> placeWish.getPlace().getId())
                .collect(Collectors.toList());

        //해당 `placeId`들로 장소 조회
        List<Place> places = placeRepository.findAllById(placeIds);

        //PlaceResponseDTO로 변환 후 반환
        return places.stream()
                .map(place -> convertToResponseDTO(place, location))
                .collect(Collectors.toList());
    }

    private PlaceResponseDTO convertToResponseDTO(Place place , LocationRequestDTO location) {
        Double distance = calculateDistance(
                location.getLatitude(),
                location.getLongitude(),
                place.getLatitude(),
                place.getLongitude()
        );

        int reviewCount = placeReviewRepository.countByPlaceId(place.getId());

        return PlaceResponseDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .distance(distance)
                .phoneNumber(place.getPhoneNumber())
                .reviewCount(reviewCount)
                .imgUrl(place.getImages().isEmpty() ? null : place.getImages().get(0).getPhotoReference())
                .build();
    }

    // 도시별 핫 플레이스 조회
    public List<PlaceResponseDTO> findHotPlacesByCity(Long cityId, LocationRequestDTO location) {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Object[]> results = placeRepository.findAllByCityIdOrderByWishCount(cityId,pageRequest);
        return results.stream()
                .map(result -> {
                    Place place = (Place) result[0];
                    Double distance = calculateDistance(
                            location.getLatitude(),
                            location.getLongitude(),
                            place.getLatitude(),
                            place.getLongitude()
                    );
                    return PlaceResponseDTO.builder()
                            .id(place.getId())
                            .name(place.getName())
                            //.address(place.getAddress())
                            .category(place.getCategory())
                            //.phoneNumber(place.getPhoneNumber())
                            .open(place.getOpen())
                            .distance(distance)
                            .imgUrl(place.getImages().isEmpty() ? null :
                                    googlePlacesService.getPhotoUrl(
                                            place.getImages().get(0).getPhotoReference(),
                                            place.getImages().get(0).getWidth()
                                    ))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 장소 생성 (관리자용)
    public Long createPlace(PlaceCreateRequestDTO request) {

        Place place = Place.builder()
                .name(request.getName())
                .address(request.getAddress())
                .category(request.getCategory())
                .phoneNumber(request.getPhoneNumber())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .open(request.getOpen())
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