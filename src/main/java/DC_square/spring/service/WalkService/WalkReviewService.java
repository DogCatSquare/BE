package DC_square.spring.service.WalkService;

import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.walk.Walk;
import DC_square.spring.domain.entity.walk.WalkReview;
import DC_square.spring.repository.WalkRepository.WalkRepository;
import DC_square.spring.repository.WalkRepository.WalkReviewRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.request.walk.WalkReviewCreateRequestDto;
import DC_square.spring.web.dto.response.walk.WalkResponseDto;
import DC_square.spring.web.dto.response.walk.WalkReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalkReviewService {

    private final WalkReviewRepository walkReviewRepository;
    private final WalkRepository walkRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;


    public WalkReviewResponseDto createWalkReview(WalkReviewCreateRequestDto request, Long walkId, String token) {
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("잘못된 토큰입니다.");
        }

        String userEmail = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Walk walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new RuntimeException("산책로를 찾을 수 없습니다."));

        WalkReview walkReview = WalkReview.builder()
                .user(user)
                .walk(walk)
                .content(request.getContent())
                .build();

        WalkReview savedWalkReview = walkReviewRepository.save(walkReview);

        WalkReviewResponseDto.WalkReviewDto reviewDto = WalkReviewResponseDto.WalkReviewDto.builder()
                .reviewId(savedWalkReview.getId())
                .walkId(savedWalkReview.getWalk().getId())
                .content(savedWalkReview.getContent())
                .createdAt(savedWalkReview.getCreatedAt())
                .updatedAt(savedWalkReview.getUpdatedAt())
                .createdBy(WalkResponseDto.CreatedByDto.builder()
                        .userId(walk.getCreatedBy().getId().toString())
                        .nickname(walk.getCreatedBy().getNickname())
                        //.breed(walk.getCreatedBy().getBreed())
                        .build())
                .build();

        return new WalkReviewResponseDto(List.of(reviewDto));
    }

    public void deleteWalkReview(Long reviewId, String token) throws RuntimeException {
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("잘못된 토큰입니다.");
        }

        String userEmail = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        WalkReview walkReview = walkReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("해당 후기를 찾을 수 없습니다."));

        if (!walkReview.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("산책로 후기 삭제 권한이 없습니다.");
        }

        walkReviewRepository.delete(walkReview);
    }

    public WalkReviewResponseDto viewWalkReviewList(Long walkId) {
        Walk walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new RuntimeException("산책로를 찾을 수 없습니다."));

        List<WalkReview> walkReviews = walkReviewRepository.findByWalk(walk);

        List<WalkReviewResponseDto.WalkReviewDto> walkReviewDtos = walkReviews.stream()
                .map(review -> WalkReviewResponseDto.WalkReviewDto.builder()
                        .reviewId(review.getId())
                        .walkId(walk.getId())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .updatedAt(review.getUpdatedAt())
                        .createdBy(WalkResponseDto.CreatedByDto.builder()
                                .userId(String.valueOf(review.getUser().getId()))
                                .nickname(review.getUser().getNickname())
                                //.breed(review.getUser().getBreed())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return WalkReviewResponseDto.builder()
                .walkReviews(walkReviewDtos)
                .build();
    }
}