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

    // 산책로 등록
    public WalkCreateResponseDto createWalk(WalkCreateRequestDto walkCreateRequestDto, Long userId) {
        // userId로 UserEntity 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Walk walk = Walk.builder()
                .title(walkCreateRequestDto.getTitle())
                .description(walkCreateRequestDto.getDescription())
                .distance(walkCreateRequestDto.getDistance())
                .time(walkCreateRequestDto.getTime())
                .difficulty(walkCreateRequestDto.getDifficulty())
                .specials(walkCreateRequestDto.getSpecial().stream()  // special 처리
                        .map(specialDto -> Special.valueOf(specialDto.getType()))  // String -> Special enum 변환
                        .collect(Collectors.toList()))
                .coordinates(walkCreateRequestDto.getCoordinates().stream()  // coordinates 처리
                        .map(coordDto -> Coordinate.builder()  // Coordinate 객체 생성
                                .latitude(coordDto.getLatitude())  // 위도
                                .longitude(coordDto.getLongitude())  // 경도
                                .sequence(coordDto.getSequence())  // 순서
                                .build())  // Coordinate 객체 빌드
                        .collect(Collectors.toList()))
                .reviewCount(0)  // 리뷰 개수 초기화
                .createdBy(user)  // 생성자 정보 (user 객체 필요)
                .build();

        Walk savedWalk = walkRepository.save(walk);

//        List<WalkResponseDto.WalkDto> walkDtos = List.of(
//                WalkResponseDto.WalkDto.builder()
//                        .walkId(savedWalk.getId())  // walkId를 설정
//                        .title(savedWalk.getTitle())
//                        .description(savedWalk.getDescription())
//                        .distance(savedWalk.getDistance())
//                        .time(savedWalk.getTime())
//                        .difficulty(savedWalk.getDifficulty().name())
//                        .special(savedWalk.getSpecials().stream()
//                                .map(special -> WalkResponseDto.SpecialDto.builder()
//                                        .type(special.name())
//                                        .build())
//                                .collect(Collectors.toList()))
//                        .coordinates(savedWalk.getCoordinates().stream()
//                                .map(coord -> WalkResponseDto.CoordinateDto.builder()
//                                        .latitude(coord.getLatitude())
//                                        .longitude(coord.getLongitude())
//                                        .sequence(coord.getSequence())
//                                        .build())
//                                .collect(Collectors.toList()))
//                        .createdAt(savedWalk.getCreatedAt())
//                        .updatedAt(savedWalk.getUpdatedAt())
//                        .createdBy(WalkResponseDto.CreatedByDto.builder()
//                                .userId(savedWalk.getCreatedBy().getId().toString())  // userId 포함
//                                .nickname(savedWalk.getCreatedBy().getNickname())    // nickname 포함
//                                .build())  // 필요한 필드만 포함
//                        .build()
//        );
//
//        return WalkResponseDto.builder()
//                .walks(walkDtos)  // WalkDto 목록을 반환
//                .build();
        return new WalkCreateResponseDto(true, "산책로 등록에 성공했습니다.", savedWalk.getId());
    }
}