package DC_square.spring.service.place;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.place.Place;
import DC_square.spring.domain.entity.place.PlaceReview;
//import DC_square.spring.domain.entity.place.PlaceReviewImage;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.place.PlaceRepository;
import DC_square.spring.repository.place.PlaceReviewRepository;
import DC_square.spring.web.dto.request.place.PlaceReviewCreateRequestDTO;
import DC_square.spring.web.dto.response.place.PlaceReviewResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceReviewService {

    private final PlaceReviewRepository placeReviewRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    public Long createPlaceReview(PlaceReviewCreateRequestDTO request, Long placeId) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // place도 마찬가지로 ID로 엔티티를 찾아옵니다
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        PlaceReview placeReview = PlaceReview.builder()
                .user(user)
                .place(place)
                .content(request.getContent())
                .createdAt(request.getCreatedAt())
                .placeReviewImageUrl(request.getPlaceReviewImageUrl())
                .build();

        return placeReviewRepository.save(placeReview).getId();
    }

    public List<PlaceReviewResponseDTO> findPlaceReviews(Long placeId) {
        List<PlaceReview> placeReviews = placeReviewRepository.findAllByPlaceId(placeId);
        return placeReviews.stream()
                .map(placeReview -> PlaceReviewResponseDTO.builder()
                        .id(placeReview.getId())
                        //.breed(placeReview.getPlace().getBreed())
                        .content(placeReview.getContent())
                        .userId(placeReview.getUser().getId())
                        .nickname(placeReview.getUser().getNickname())
                        //.userImageUrl(placeReview.getUser().getImageUrl())
                        .createdAt(placeReview.getCreatedAt().toString())
                        .placeReviewImageUrl(placeReview.getPlaceReviewImageUrl())
                        .placeId(placeReview.getPlace().getId())
                        .build())
                .collect(Collectors.toList());
    }

    public void deletePlaceReview(Long reviewId, Long userId) {

        PlaceReview review = placeReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        if(!review.getUser().getId().equals(userId)){
            throw new RuntimeException("삭제할 권한이 없습니다.");
        }

        placeReviewRepository.deleteById(reviewId);
    }
}
