package DC_square.spring.service.place;

import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.place.*;
import DC_square.spring.domain.entity.region.City;
import DC_square.spring.domain.entity.region.Province;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.place.*;
import DC_square.spring.web.dto.request.place.LocationRequestDTO;
import DC_square.spring.web.dto.request.place.PlaceCreateRequestDTO;
import DC_square.spring.web.dto.request.place.PlaceUserInfoUpdateDTO;
import DC_square.spring.web.dto.response.place.PlaceDetailResponseDTO;
import DC_square.spring.web.dto.response.place.PlacePageResponseDTO;
import DC_square.spring.web.dto.response.place.PlaceResponseDTO;
import DC_square.spring.domain.enums.PlaceCategory;
import DC_square.spring.web.dto.response.place.PlaceReviewResponseDTO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
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
    private final PlaceViewRepository placeViewRepository;

    /**
     * 키워드로 장소 검색 (거리 제한 없음)
     */
    public PlacePageResponseDTO<PlaceResponseDTO> searchPlacesByKeyword(String keyword, LocationRequestDTO location, int page, int size) {
        Map<String, Object> searchResults = googlePlacesService.searchPlacesByKeyword(keyword);
        List<Map<String, Object>> results = (List<Map<String, Object>>) searchResults.get("results");

        if (results == null || results.isEmpty()) {
            return PlacePageResponseDTO.of(new ArrayList<>(), page, size);
        }

        // 각 장소의 유사도 점수를 미리 계산하여 저장
        Map<String, Double> similarityScores = new HashMap<>();
        for (Map<String, Object> result : results) {
            String name = (String) result.get("name");
            double similarity = calculateSimilarity(name.toLowerCase(), keyword.toLowerCase());
            similarityScores.put(name, similarity);
        }

        List<PlaceResponseDTO> responseDTOs = results.stream()
                //.filter(this::isPetRelatedPlace)
                .map(result -> saveAndConvertToDTO(result, location))
                .filter(Objects::nonNull)
                .sorted((a, b) -> {
                    // 1. 먼저 유사도로 비교
                    double similarityA = similarityScores.get(a.getName());
                    double similarityB = similarityScores.get(b.getName());
                    int similarityCompare = Double.compare(similarityB, similarityA);

                    // 2. 유사도가 같은 경우 거리로 비교
                    if (similarityCompare == 0) {
                        return Double.compare(a.getDistance(), b.getDistance());
                    }
                    return similarityCompare;
                })
                .collect(Collectors.toList());

        return PlacePageResponseDTO.of(responseDTOs, page, size);
    }

    private int compareByNameSimilarity(String nameA, String nameB, String keyword) {
        nameA = nameA.toLowerCase();
        nameB = nameB.toLowerCase();
        keyword = keyword.toLowerCase();

        // 1. 정확히 일치하는 경우 최우선
        if (nameA.equals(keyword) && !nameB.equals(keyword)) return -1;
        if (!nameA.equals(keyword) && nameB.equals(keyword)) return 1;

        // 2. 검색어로 시작하는 경우 다음 우선
        if (nameA.startsWith(keyword) && !nameB.startsWith(keyword)) return -1;
        if (!nameA.startsWith(keyword) && nameB.startsWith(keyword)) return 1;

        // 3. 검색어가 포함된 경우 그 다음
        if (nameA.contains(keyword) && !nameB.contains(keyword)) return -1;
        if (!nameA.contains(keyword) && nameB.contains(keyword)) return 1;

        // 4. 그 외의 경우 Jaro-Winkler 유사도로 비교
        double similarityA = calculateSimilarity(nameA, keyword);
        double similarityB = calculateSimilarity(nameB, keyword);
        return Double.compare(similarityB, similarityA);
    }

    /**
     * Jaro-Winkler 유사도 계산
     */
    private double calculateSimilarity(String str1, String str2) {
        int commonChars = 0;
        int maxDistance = Math.max(str1.length(), str2.length()) / 2 - 1;

        StringBuilder s1Common = new StringBuilder();
        StringBuilder s2Common = new StringBuilder();

        // 공통 문자 찾기
        for (int i = 0; i < str1.length(); i++) {
            char ch1 = str1.charAt(i);
            boolean found = false;

            int start = Math.max(0, i - maxDistance);
            int end = Math.min(i + maxDistance + 1, str2.length());

            for (int j = start; j < end; j++) {
                if (str2.charAt(j) == ch1) {
                    found = true;
                    commonChars++;
                    s1Common.append(ch1);
                    s2Common.append(ch1);
                    break;
                }
            }
        }

        if (commonChars == 0) return 0.0;

        // 전치 문자 수 계산
        int transpositions = 0;
        for (int i = 0; i < s1Common.length(); i++) {
            if (s1Common.charAt(i) != s2Common.charAt(i)) {
                transpositions++;
            }
        }
        transpositions /= 2;

        // Jaro 거리 계산
        return ((double) commonChars / str1.length() +
                (double) commonChars / str2.length() +
                (double) (commonChars - transpositions) / commonChars) / 3.0;
    }
    /**
     * 주변 장소 검색 (카테고리 기반)
     */
    public PlacePageResponseDTO<PlaceResponseDTO> findNearbyPlaces(
            LocationRequestDTO location,
            int page,
            int size
    ) {
        Map<String, Object> searchResults = googlePlacesService.searchNearbyPlaces(
                location.getLatitude(),
                location.getLongitude()
        );

        List<Map<String, Object>> results = (List<Map<String, Object>>) searchResults.get("results");

        if (results == null || results.isEmpty()) {
            return PlacePageResponseDTO.of(new ArrayList<>(), page, size);
        }

        List<PlaceResponseDTO> responseDTOs = results.stream()
                .map(result -> saveAndConvertToDTO(result, location))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(PlaceResponseDTO::getDistance))
                .collect(Collectors.toList());

        return PlacePageResponseDTO.of(responseDTOs, page, size);
    }

    private PlaceResponseDTO saveAndConvertToDTO(Map<String, Object> placeData, LocationRequestDTO location) {
        String googlePlaceId = (String) placeData.get("place_id");

        Place existingPlace = placeRepository.findByGooglePlaceId(googlePlaceId).orElse(null);
        if (existingPlace != null) {
            return convertToResponseDTO(existingPlace, location);
        }

        Map<String, Object> details = googlePlacesService.getPlaceDetails(googlePlaceId);

        Map<String, Object> detailResult = (Map<String, Object>) details.get("result");

        String address = (String) detailResult.get("formatted_address");
        Pair<Province, City> regionInfo = extractRegionInfo(address);

        Place newPlace = createPlaceFromGoogleData(placeData, detailResult, regionInfo);
        Place savedPlace = placeRepository.save(newPlace);

        PlaceDetail placeDetail = createPlaceDetail(savedPlace, detailResult);
        placeDetailRepository.save(placeDetail);

        saveGooglePlaceImages(detailResult, savedPlace);

        return convertToResponseDTO(savedPlace, location);
    }

    private boolean isPetRelatedPlace(Map<String, Object> placeData) {
        String name = ((String) placeData.get("name")).toLowerCase();

        List<String> petKeywords = Arrays.asList(
                "애견", "펫", "동물", "쥬", "pet", "animal", "zoo",
                "강아지", "고양이", "독", "도그", "dog", "cat", "멍", "댕", "냥",
                "공원", "park", "veterinary"
        );

        return petKeywords.stream()
                .anyMatch(keyword -> name.contains(keyword));
    }

    private Place createPlaceFromGoogleData(Map<String, Object> placeData, Map<String, Object> detailResult, Pair<Province, City> regionInfo) {
        Map<String, Object> geometry = (Map<String, Object>) placeData.get("geometry");
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");
        Map<String, Object> openingHours = (Map<String, Object>) placeData.get("opening_hours");

        String phoneNumber = detailResult != null ? (String) detailResult.get("formatted_phone_number") : null;

        return Place.builder()
                .name((String) placeData.get("name"))
                .address((String) detailResult.get("formatted_address"))
                .category(determinePlaceCategory(placeData))
                .phoneNumber(phoneNumber)
                .latitude((Double) location.get("lat"))
                .longitude((Double) location.get("lng"))
                .googlePlaceId((String) placeData.get("place_id"))
                .open(openingHours != null ? (Boolean) openingHours.get("open_now") : null)
                .images(new ArrayList<>())
                .keywords(new ArrayList<>())
                .province(regionInfo != null ? regionInfo.getFirst() : null)
                .city(regionInfo != null ? regionInfo.getSecond() : null)
                .build();
    }

    private PlaceDetail createPlaceDetail(Place place, Map<String, Object> detailResult) {
        String businessHours = null;
        if (detailResult != null && detailResult.containsKey("opening_hours")) {
            Map<String, Object> openingHours = (Map<String, Object>) detailResult.get("opening_hours");
            if (openingHours.containsKey("weekday_text")) {
                businessHours = openingHours.get("weekday_text").toString();
            }
        }

        String description = null;
        if (detailResult != null && detailResult.containsKey("editorial_summary")) {
            Map<String, Object> editorialSummary = (Map<String, Object>) detailResult.get("editorial_summary");
            description = (String) editorialSummary.get("overview");
        }

        return PlaceDetail.builder()
                .place(place)
                .businessHours(businessHours)
                .homepageUrl(detailResult != null ? (String) detailResult.get("website") : null)
                .description(description)
                .facilities(new ArrayList<>())
                .build();
    }

    private void saveGooglePlaceImages(Map<String, Object> detailResult, Place place) {
        if (detailResult == null || !detailResult.containsKey("photos")) {
            return;
        }

        List<Map<String, Object>> photos = (List<Map<String, Object>>) detailResult.get("photos");
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

    private Pair<Province, City> extractRegionInfo(String fullAddress) {
        // 1. 모든 Province를 가져옴
        List<Province> provinces = em.createQuery("SELECT p FROM Province p", Province.class)
                .getResultList();

        // 2. 주소에서 province 찾기
        Province matchedProvince = provinces.stream()
                .filter(province -> fullAddress.contains(normalizeProvinceName(province.getName())))
                .findFirst()
                .orElse(null);

        if (matchedProvince == null) {
            return null;
        }

        // 3. 해당 province의 모든 city 가져오기
        List<City> cities = em.createQuery(
                        "SELECT c FROM City c WHERE c.province.id = :provinceId", City.class)
                .setParameter("provinceId", matchedProvince.getId())
                .getResultList();

        // 4. 주소에서 city 찾기
        City matchedCity = cities.stream()
                .filter(city -> fullAddress.contains(city.getName()))
                .findFirst()
                .orElse(null);

        return matchedCity != null ? Pair.of(matchedProvince, matchedCity) : null;
    }

    private String normalizeProvinceName(String name) {
        Map<String, String> provinceMap = new HashMap<>();
        provinceMap.put("서울", "서울특별시");
        provinceMap.put("부산", "부산광역시");
        provinceMap.put("대구", "대구광역시");
        provinceMap.put("인천", "인천광역시");
        provinceMap.put("광주", "광주광역시");
        provinceMap.put("대전", "대전광역시");
        provinceMap.put("울산", "울산광역시");
        provinceMap.put("세종", "세종특별자치시");
        provinceMap.put("충북", "충청북도");
        provinceMap.put("충남", "충청남도");
        provinceMap.put("전북", "전라북도");
        provinceMap.put("전남", "전라남도");
        provinceMap.put("경북", "경상북도");
        provinceMap.put("경남", "경상남도");
        provinceMap.put("제주", "제주특별자치도");
        return provinceMap.getOrDefault(name, name);
    }

    //카테고리 필터 로직 (type으로 구분)
    private PlaceCategory determinePlaceCategory(Map<String, Object> placeData) {
        List<String> types = (List<String>) placeData.get("types");
        if (types == null) return PlaceCategory.ETC;

        if (types.contains("veterinary_care")) return PlaceCategory.HOSPITAL;
        if (types.contains("cafe")) return PlaceCategory.CAFE;
        if (types.contains("park")) return PlaceCategory.PARK;
        if (types.contains("lodging")) return PlaceCategory.HOTEL;
        if (types.contains("pet_store")) return PlaceCategory.ETC;

        return PlaceCategory.ETC;
//        String name = ((String) placeData.get("name")).toLowerCase();
//
//        if (name.contains("병원") || name.contains("수의") || name.contains("의료")
//                || name.contains("메디컬") || name.contains("hospital")) {
//            return PlaceCategory.HOSPITAL;
//        }
//        if (name.contains("호텔") || name.contains("hotel")) {
//            return PlaceCategory.HOTEL;
//        }
//        if (name.contains("카페") || name.contains("cafe")) {
//            return PlaceCategory.CAFE;
//        }
//        if (name.contains("공원") || name.contains("운동장") || name.contains("파크") || name.contains("park")) {
//            return PlaceCategory.PARK;
//        }
//        return PlaceCategory.ETC;
    }

    private PlaceResponseDTO convertToResponseDTO(Place place, LocationRequestDTO location) {
        Double distance = null;
        if (location != null) {
            distance = calculateDistance(
                    location.getLatitude(),
                    location.getLongitude(),
                    place.getLatitude(),
                    place.getLongitude()
            );
        }

        return PlaceResponseDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .category(place.getCategory())
                .phoneNumber(place.getPhoneNumber())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .distance(distance)
                .open(place.getOpen())
                .imgUrl(place.getImages().isEmpty() ? null :
                        googlePlacesService.getPhotoUrl(
                                place.getImages().get(0).getPhotoReference(),
                                place.getImages().get(0).getWidth()
                        ))
                .reviewCount(placeReviewRepository.countByPlaceId(place.getId()))
                .keywords(place.getKeywords())
                .build();
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

    // 위시리스트 조회
    public PlacePageResponseDTO<PlaceResponseDTO> findWishList(String token, LocationRequestDTO location, int page, int size) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        List<Long> placeIds = placeWishRepository.findAllByUserId(user.getId()).stream()
                .map(placeWish -> placeWish.getPlace().getId())
                .collect(Collectors.toList());

        List<Place> places = placeRepository.findAllById(placeIds);

        List<PlaceResponseDTO> responseDTOs = places.stream()
                .map(place -> convertToResponseDTO(place, location))
                .collect(Collectors.toList());

        return PlacePageResponseDTO.of(responseDTOs, page, size);
    }

    // 도시별 핫플레이스 조회
    public List<PlaceResponseDTO> findHotPlacesByCity(Long cityId, LocationRequestDTO location) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        PageRequest pageRequest = PageRequest.of(0, 4);

        List<Object[]> results = placeViewRepository.findTopPlacesByViewCount(weekAgo, cityId);
        //List<Object[]> results = placeRepository.findAllByCityIdOrderByWishCount(cityId, pageRequest);

        return results.stream()
                .limit(4)
                .map(result -> {
                    Long placeId = (Long) result[0];
                    Place place = placeRepository.findById(placeId)
                            .orElseThrow(() -> new RuntimeException("Place not found"));

                    return PlaceResponseDTO.builder()
                            .id(place.getId())
                            .name(place.getName())
                            //.address(place.getAddress())
                            .category(place.getCategory())
                            //.phoneNumber(place.getPhoneNumber())
                            //.latitude(place.getLatitude())
                            //.longitude(place.getLongitude())
                            .open(place.getOpen())
                            .distance(calculateDistance(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    place.getLatitude(),
                                    place.getLongitude()
                            ))
                            //.reviewCount(placeReviewRepository.countByPlaceId(place.getId()))
                            //.keywords(place.getKeywords())
                            .imgUrl(place.getImages().isEmpty() ? null :
                                    googlePlacesService.getPhotoUrl(
                                            place.getImages().get(0).getPhotoReference(),
                                            place.getImages().get(0).getWidth()
                                    ))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void increaseViewCount(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        // 기존 view 카운트 증가
        place.setView(place.getView() + 1);

        // 조회 기록 저장
        PlaceView placeView = PlaceView.builder()
                .place(place)
                .viewedAt(LocalDateTime.now())
                .build();

        placeViewRepository.save(placeView);
    }

    // 장소 정보 업데이트
    public void updatePlaceUserInfo(Long placeId, PlaceUserInfoUpdateDTO updateDTO) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));

        if (place.getKeywords() == null) {
            place.setKeywords(new ArrayList<>());
        }
        if (updateDTO.getKeywords() != null) {
            place.getKeywords().addAll(updateDTO.getKeywords());
        }
        placeRepository.save(place);

        PlaceDetail placeDetail = placeDetailRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소의 상세 정보가 존재하지 않습니다."));
        placeDetail.setAdditionalInfo(updateDTO.getAdditionalInfo());
        placeDetailRepository.save(placeDetail);
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
                .keywords(request.getKeywords())
                .build();

        Place savedPlace = placeRepository.save(place);

        PlaceDetail placeDetail = PlaceDetail.builder()
                .place(savedPlace)
                .businessHours(request.getBusinessHours())
                .homepageUrl(request.getHomepageUrl())
                .description(request.getDescription())
                .additionalInfo(request.getAdditionalInfo())
                .build();

        placeDetailRepository.save(placeDetail);

        return savedPlace.getId();
    }

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
                .reviewCount(reviewCount)
                .recentReviews(recentReviewDtos)
                .keywords(place.getKeywords())
                .additionalInfo(placeDetail.getAdditionalInfo())
                .build();
    }
}