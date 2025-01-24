package DC_square.spring.service.WalkService;

import DC_square.spring.domain.entity.Walk;
import DC_square.spring.domain.entity.Coordinate;
import DC_square.spring.domain.enums.Special;
import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.WalkRepository.WalkRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.request.walk.WalkRequestDto;
import DC_square.spring.web.dto.request.walk.WalkCreateRequestDto;
import DC_square.spring.web.dto.response.walk.WalkCreateResponseDto;
import DC_square.spring.web.dto.response.walk.WalkDetailResponseDto;
import DC_square.spring.web.dto.response.walk.WalkResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalkService {

    private final WalkRepository walkRepository;
    private final UserRepository userRepository;

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

    // 산책로 상세 조회
    public WalkDetailResponseDto getWalkDetails(Long walkId) {
        Walk walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new IllegalArgumentException("Walk not found for id: " + walkId));

        List<WalkResponseDto.CoordinateDto> startCoordinates = walk.getCoordinates().stream()
                .filter(coord -> coord.getSequence() == 1)
                .map(coord -> WalkResponseDto.CoordinateDto.builder()
                        .latitude(coord.getLatitude())
                        .longitude(coord.getLongitude())
                        .sequence(coord.getSequence())
                        .build())
                .collect(Collectors.toList());

        List<WalkResponseDto.CoordinateDto> endCoordinates = walk.getCoordinates().stream()
                .filter(coord -> coord.getSequence() == walk.getCoordinates().size())
                .map(coord -> WalkResponseDto.CoordinateDto.builder()
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

    public WalkCreateResponseDto createWalk(WalkCreateRequestDto walkCreateRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Walk walk = Walk.builder()
                .title(walkCreateRequestDto.getTitle())
                .description(walkCreateRequestDto.getDescription())
                .distance(walkCreateRequestDto.getDistance())
                .time(walkCreateRequestDto.getTime())
                .difficulty(walkCreateRequestDto.getDifficulty())
                .specials(walkCreateRequestDto.getSpecial().stream()
                        .map(specialDto -> Special.valueOf(specialDto.getType()))
                        .collect(Collectors.toList()))
                .coordinates(walkCreateRequestDto.getCoordinates().stream()
                        .map(coordDto -> Coordinate.builder()
                                .latitude(coordDto.getLatitude())
                                .longitude(coordDto.getLongitude())
                                .sequence(coordDto.getSequence())
                                .build())
                        .collect(Collectors.toList()))
                .reviewCount(0)
                .createdBy(user)
                .build();

        Walk savedWalk = walkRepository.save(walk);

        return new WalkCreateResponseDto(true, "산책로 등록에 성공했습니다.", savedWalk.getId());
    }

    public void deleteWalk(Long walkId, Long userId) throws RuntimeException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        Walk walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new IllegalArgumentException("산책로를 찾을 수 없습니다."));

        if (!walk.getCreatedBy().equals(user)) {
            throw new RuntimeException("산책로 삭제 권한이 없습니다.");
        }

        walkRepository.delete(walk);
    }
}