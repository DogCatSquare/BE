package DC_square.spring.service.WalkService;

import DC_square.spring.domain.entity.Walk;
import DC_square.spring.repository.WalkRepository.WalkRepository;
import DC_square.spring.web.dto.request.WalkRequestDto;
import DC_square.spring.web.dto.response.WalkDetailResponseDto;
import DC_square.spring.web.dto.response.WalkResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalkService {

    private final WalkRepository walkRepository;

    // 산책로 목록 조회
    public WalkResponseDto viewWalkList(WalkRequestDto walkRequestDto) {
        // 사용자 요청에 따라 가까운 산책로를 가져오는 로직 (예: 위도와 경도를 기준으로 검색)
        List<Walk> walks = walkRepository.findNearbyWalks(
                walkRequestDto.getLatitude(),
                walkRequestDto.getLongitude(),
                walkRequestDto.getRadius()
        );

        List<WalkResponseDto.WalkDto> walkDtos = walks.stream()
                .map(walk -> WalkResponseDto.WalkDto.builder()
                        .walkId(walk.getId())
                        .title(walk.getTitle())
                        .description(walk.getDescription())
                        //.reviewCount(walk.getReviewCount())
                        .distance(walk.getDistance())
                        .time(walk.getTime())
                        .difficulty(walk.getDifficulty().name())
                        .special(walk.getSpecials().stream()
                                .map(special -> WalkResponseDto.SpecialDto.builder()
                                        .type(special.name())
                                        .build())
                                .collect(Collectors.toList()))
                        .coordinates(walk.getCoordinates().stream()
                                .map(coord -> WalkResponseDto.CoordinateDto.builder()
                                        .latitude(coord.getLatitude())
                                        .longitude(coord.getLongitude())
                                        .sequence(coord.getSequence())
                                        .build())
                                .collect(Collectors.toList()))
                        .createdAt(walk.getCreatedAt())
                        .updatedAt(walk.getUpdatedAt())
                        .createdBy(WalkResponseDto.CreatedByDto.builder()
                                .userId(walk.getCreatedBy().getId().toString())
                                .nickname(walk.getCreatedBy().getNickname())
                                //.breed(walk.getCreatedBy().getBreed())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return WalkResponseDto.builder()
                .walks(walkDtos)
                .build();
    }

    public WalkDetailResponseDto getWalkDetails(Long walkId) {
        Walk walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new IllegalArgumentException("Walk not found for id: " + walkId));

        List<WalkResponseDto.CoordinateDto> startCoordinates = walk.getCoordinates().stream()
                .filter(coord -> coord.getSequence() == 1) // 시작 좌표 (sequence가 1인 좌표)
                .map(coord -> WalkResponseDto.CoordinateDto.builder() // 수정된 부분
                        .latitude(coord.getLatitude())
                        .longitude(coord.getLongitude())
                        .sequence(coord.getSequence())
                        .build())
                .collect(Collectors.toList());

        List<WalkResponseDto.CoordinateDto> endCoordinates = walk.getCoordinates().stream()
                .filter(coord -> coord.getSequence() == walk.getCoordinates().size()) // 종료 좌표 (sequence가 마지막인 좌표)
                .map(coord -> WalkResponseDto.CoordinateDto.builder() // 수정된 부분
                        .latitude(coord.getLatitude())
                        .longitude(coord.getLongitude())
                        .sequence(coord.getSequence())
                        .build())
                .collect(Collectors.toList());

        return WalkDetailResponseDto.builder()
                .walkId(walk.getId())
                .title(walk.getTitle())
                .description(walk.getDescription())
                .distance(walk.getDistance())
                .time(walk.getTime())
                .difficulty(walk.getDifficulty().name())
                .special(walk.getSpecials().stream()
                        .map(special -> WalkResponseDto.SpecialDto.builder()
                                .type(special.name())
                                .build())
                        .collect(Collectors.toList()))
                .startCoordinates(startCoordinates)
                .endCoordinates(endCoordinates)
                .createdAt(walk.getCreatedAt())
                .updatedAt(walk.getUpdatedAt())
                .createdBy(WalkResponseDto.CreatedByDto.builder()
                        .userId(walk.getCreatedBy().getId().toString())
                        .nickname(walk.getCreatedBy().getNickname())
                        //.breed(walk.getCreatedBy().getBreed())
                        .build())
                .build();
    }
}