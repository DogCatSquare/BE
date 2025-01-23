package DC_square.spring.service.place;

import DC_square.spring.config.S3.AmazonS3Manager;
import DC_square.spring.config.S3.Uuid;
import DC_square.spring.config.S3.UuidRepository;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.place.Place;
import DC_square.spring.domain.entity.place.PlaceReview;
//import DC_square.spring.domain.entity.place.PlaceReviewImage;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.place.PlaceRepository;
import DC_square.spring.repository.place.PlaceReviewLikeRepository;
import DC_square.spring.repository.place.PlaceReviewRepository;
import DC_square.spring.web.dto.request.place.PlaceReviewCreateRequestDTO;
import DC_square.spring.web.dto.response.place.PlaceReviewResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceReviewService {

    private final PlaceReviewRepository placeReviewRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceReviewLikeRepository placeReviewLikeRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    public Long createPlaceReview(PlaceReviewCreateRequestDTO request, Long placeId, List<MultipartFile> images) {
        if (images.isEmpty()) {
            throw new RuntimeException("후기 이미지는 필수 입니다.");
        }

        List<String> imageUrls = images.stream()
                .map(image -> {
                    String uuid = UUID.randomUUID().toString();
                    Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
                    return s3Manager.uploadFile(s3Manager.generateReview(savedUuid), image);
                })
                .collect(Collectors.toList());

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
                .placeReviewImageUrl(imageUrls)
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
                        .isLiked(placeReviewLikeRepository.existsByUserIdAndPlaceReviewId(placeReview.getId(), placeReview.getUser().getId()))
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
