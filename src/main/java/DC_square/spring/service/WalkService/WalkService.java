package DC_square.spring.service.WalkService;

import DC_square.spring.domain.entity.Walk;
import DC_square.spring.repository.WalkRepository;
import DC_square.spring.web.dto.request.WalkRequestDto;
import DC_square.spring.web.dto.response.WalkResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
                walkRequestDto.getLongitude()
        );

        // Entity -> WalkResponseDto.WalkDto 변환
        List<WalkResponseDto.WalkDto> walkDtos = walks.stream()
                .map(walk -> WalkResponseDto.WalkDto.builder()
                        .walkId(walk.getId())
                        .title(walk.getTitle())
                        .description(walk.getDescription())
                        .reviewCount(walk.getReviewCount())
                        .distance(walk.getDistance())
                        .time(walk.getTime())
                        .difficulty(walk.getDifficulty().name()) // Enum 값을 문자열로 변환
                        .special(walk.getSpecials().stream()
                                .map(special -> WalkResponseDto.SpecialDto.builder()
                                        .type(special.name()) // Enum 값을 문자열로 변환
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
                                .userId(walk.getCreatedBy().getId().toString()) // ID를 문자열로 변환
                                .nickname(walk.getCreatedBy().getNickname())
                                //.breed(walk.getCreatedBy().getBreed())
                                .build())
                        .build())
                .collect(Collectors.toList());

        // WalkResponseDto 생성
        return WalkResponseDto.builder()
                .walks(walkDtos)
                .build();
    }
}
