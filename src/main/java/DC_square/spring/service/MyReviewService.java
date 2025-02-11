package DC_square.spring.service;

import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.WalkRepository.WalkReviewRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.place.PlaceReviewRepository;
import DC_square.spring.web.dto.response.ReviewResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyReviewService {
    private final PlaceReviewRepository placeReviewRepository;
    private final WalkReviewRepository walkReviewRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public List<ReviewResponseDTO> getMyReviews(String token) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<ReviewResponseDTO> allReviews = new ArrayList<>();

        // 장소 리뷰 추가
        placeReviewRepository.findAllByUserId(user.getId())
                .forEach(review -> allReviews.add(
                        ReviewResponseDTO.builder()
                                .id(review.getId())
                                .content(review.getContent())
                                .createdAt(review.getCreatedAt())
                                .imageUrls(review.getPlaceReviewImageUrl())
                                .build()
                ));

        // 산책로 리뷰 추가
        walkReviewRepository.findAllByUserId(user.getId())
                .forEach(review -> allReviews.add(
                        ReviewResponseDTO.builder()
                                .id(review.getId())
                                .content(review.getContent())
                                .createdAt(review.getCreatedAt())
                                .imageUrls(review.getWalkReviewImageUrl())
                                .build()
                ));

        // 날짜순 정렬
        allReviews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return allReviews;
    }
}
