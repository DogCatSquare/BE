package DC_square.spring.service.place;


import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.place.Place;
import DC_square.spring.domain.entity.place.PlaceReview;
import DC_square.spring.domain.entity.place.PlaceReviewImage;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.place.PlaceRepository;
import DC_square.spring.repository.place.PlaceReviewRepository;
import DC_square.spring.web.dto.request.place.PlaceReviewCreateRequestDTO;
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
    private final UserRepository UserRepository;

    public Long createPlaceReview(PlaceReviewCreateRequestDTO request, Long placeId) {

        User user = UserRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // place도 마찬가지로 ID로 엔티티를 찾아옵니다
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        // URL 리스트를 PlaceReviewImage 엔티티 리스트로 변환
        List<PlaceReviewImage> reviewImages = request.getImg_urls().stream()
                .map(url -> PlaceReviewImage.builder()
                        .imageUrl(url)
                        .build())
                .collect(Collectors.toList());

        PlaceReview placeReview = PlaceReview.builder()
                .user(user)
                .place(place)
                .content(request.getContent())
                .img_urls(reviewImages)
                .createdAt(request.getCreatedAt())
                .placeId(placeId)
                .build();

        return placeReviewRepository.save(placeReview).getId();
    }
}
