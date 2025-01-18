package DC_square.spring.service.place;

import DC_square.spring.domain.entity.Region;
import DC_square.spring.domain.entity.place.PlaceDetail;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;
    private final PlaceDetailRepository PlaceDetailRepository;

    public Long createPlace(PlaceCreateRequestDTO request, Long regionId) {
        // Region ID 조회
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지역이 존재하지 않습니다."));

        // RequestDTO -> Entity 변환
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
        // 2. DB 저장
        Place savedPlace = placeRepository.save(place);

        // PlaceDetail 생성
        PlaceDetail placeDetail = PlaceDetail.builder()
                .place(savedPlace) // Place 엔티티와 연관관계 설정
                .businessHours(request.getBusinessHours())
                .homepageUrl(request.getHomepageUrl())
                .description(request.getDescription())
                .facilities(request.getFacilities())
                .build();

        // 3. DB 저장
        PlaceDetailRepository.save(placeDetail);

        return savedPlace.getId();
    }

    public List<PlaceResponseDTO> findPlaces(Long regionId, PlaceRequestDTO request) {
        // 1. DB 조회
        List<Place> places = placeRepository.findPlacesByRegionId(regionId);

        // 2. Entity -> DTO 변환 및 거리 계산
        return places.stream()
                .map(place -> convertToDTO(place, request)) // list기 때문에 컨버터를 재사용
                .collect(Collectors.toList());
    }

    public PlaceResponseDTO findPlaceById( Integer placeId) {
        // 1. DB 조회
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));

        // 2. Entity -> DTO 변환
        return convertToDTO(place, null);
    }

    // Entity -> ResponseDTO 변환
    private PlaceResponseDTO convertToDTO(Place place, PlaceRequestDTO requestDTO) {
        // 사용자 위치 기반 거리 계산
        Float distance = calculateDistance( requestDTO.getLatitude(), requestDTO.getLongitude(),
                                            place.getLatitude(), place.getLongitude());

        // DTO 생성 및 반환
        return PlaceResponseDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .category(place.getCategory())
                .phoneNumber(place.getPhoneNumber())
                .distance(distance)
                .open(place.getOpen())
                .regionId(place.getRegion().getId())  // Region 엔티티에서 ID만 추출
                .build();
    }

    //거리 게산 알고리즘
    private Float calculateDistance(Double userLat, Double userLon,
                                    Double placeLat, Double placeLon) {
        if (userLat == null || userLon == null || placeLat == null || placeLon == null) {
            return null;
        }

        final int EARTH_RADIUS = 6371; // 지구의 반지름 (km)

        double latDistance = Math.toRadians(placeLat - userLat);
        double lonDistance = Math.toRadians(placeLon - userLon);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(placeLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (EARTH_RADIUS * c * 1000); // m 단위로 반환
    }

    // 장소 상세 조회
    public PlaceDetailResponseDTO findPlaceDetailById(Integer placeId) {
        // 1. DB 조회
        PlaceDetail placeDetail = PlaceDetailRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));

        // 2. Entity -> DTO 변환
        return PlaceDetailResponseDTO.builder()
                .id(placeDetail.getId())
                .name(placeDetail.getPlace().getName())
                .address(placeDetail.getPlace().getAddress())
                .category(placeDetail.getPlace().getCategory().name())
                .phoneNumber(placeDetail.getPlace().getPhoneNumber())
                .open(placeDetail.getPlace().getOpen())
                .longitude(placeDetail.getPlace().getLongitude())
                .latitude(placeDetail.getPlace().getLatitude())
                .businessHours(placeDetail.getBusinessHours())
                .homepageUrl(placeDetail.getHomepageUrl())
                .description(placeDetail.getDescription())
                .facilities(placeDetail.getFacilities())
                .build();
    }
}