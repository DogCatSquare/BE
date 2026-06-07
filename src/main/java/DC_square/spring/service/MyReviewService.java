package DC_square.spring.service;

import DC_square.spring.apiPayload.code.status.ErrorStatus;
import DC_square.spring.apiPayload.exception.GeneralException;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.place.PlaceReview;
import DC_square.spring.domain.entity.walk.WalkReview;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.place.PlaceReviewRepository;
import DC_square.spring.repository.walk.WalkReviewRepository;
import DC_square.spring.web.dto.response.ReviewResponseDTO;
import DC_square.spring.web.dto.response.place.PlacePageResponseDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyReviewService {

  private final PlaceReviewRepository placeReviewRepository;
  private final WalkReviewRepository walkReviewRepository;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional(readOnly = true)
  public PlacePageResponseDTO<ReviewResponseDTO> getMyReviews(String token, int page, int size) {
    String userEmail = jwtTokenProvider.getUserEmail(token);
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    List<ReviewResponseDTO> allReviews = new ArrayList<>();

    // 장소 리뷰 추가
    placeReviewRepository.findAllByUserId(user.getId())
        .forEach(review -> allReviews.add(
            ReviewResponseDTO.builder()
                .id(review.getId())
                .title(review.getPlace().getName())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .imageUrls(new ArrayList<>(review.getPlaceReviewImageUrl() != null ? review.getPlaceReviewImageUrl() : new ArrayList<>()))
                .placeId(review.getPlace().getId())
                .googlePlaceId(review.getPlace().getGooglePlaceId())
                .walkId(null)
                .build()
        ));

    // 산책로 리뷰 추가
    walkReviewRepository.findAllByUserId(user.getId())
        .forEach(review -> allReviews.add(
            ReviewResponseDTO.builder()
                .id(review.getId())
                .title(review.getWalk().getTitle())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .imageUrls(new ArrayList<>(review.getWalkReviewImageUrl() != null ? review.getWalkReviewImageUrl() : new ArrayList<>()))
                .placeId(null)
                .walkId(review.getWalk().getId())
                .build()
        ));

    // 날짜순 정렬
    allReviews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

    return PlacePageResponseDTO.of(allReviews, page, size);
  }

  @Transactional
  public void deleteReview(String token, Long reviewId, String type) {
    String userEmail = jwtTokenProvider.getUserEmail(token);
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

    if ("place".equalsIgnoreCase(type)) {
      PlaceReview review = placeReviewRepository.findById(reviewId)
          .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));
      if (!review.getUser().getId().equals(user.getId())) {
        throw new GeneralException(ErrorStatus.REVIEW_UNAUTHORIZED);
      }
      placeReviewRepository.delete(review);
    } else if ("walk".equalsIgnoreCase(type)) {
      WalkReview review = walkReviewRepository.findById(reviewId)
          .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));
      if (!review.getUser().getId().equals(user.getId())) {
        throw new GeneralException(ErrorStatus.REVIEW_UNAUTHORIZED);
      }
      walkReviewRepository.delete(review);
    } else {
      throw new GeneralException(ErrorStatus.REVIEW_TYPE_INVALID);
    }
  }
}
