package DC_square.spring.service.WalkService;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.Walk;
import DC_square.spring.domain.entity.WalkReview;
import DC_square.spring.repository.WalkRepository.WalkRepository;
import DC_square.spring.repository.WalkRepository.WalkReviewRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.request.walk.WalkReviewCreateRequestDto;
import DC_square.spring.web.dto.response.walk.WalkResponseDto;
import DC_square.spring.web.dto.response.walk.WalkReviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkReviewService {

    private final WalkReviewRepository walkReviewRepository;
    private final WalkRepository walkRepository;
    private final UserRepository userRepository;

    public WalkReviewResponseDto createWalkReview(WalkReviewCreateRequestDto request, Long walkId) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

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

    public void deleteWalkReview(Long reviewId, Long userId) throws RuntimeException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        WalkReview walkReview = walkReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("해당 후기를 찾을 수 없습니다."));

        if (!walkReview.getUser().getId().equals(userId)) {
            throw new RuntimeException("산책로 후기 삭제 권한이 없습니다.");
        }

        walkReviewRepository.delete(walkReview);
    }
}