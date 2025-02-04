package DC_square.spring.service.WalkService;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.walk.Walk;
import DC_square.spring.domain.entity.walk.WalkWish;
import DC_square.spring.repository.WalkRepository.WalkRepository;
import DC_square.spring.repository.WalkRepository.WalkWishRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.web.dto.response.walk.WalkWishResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkWishService {

    private final WalkWishRepository walkWishRepository;
    private final WalkRepository walkRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public WalkWishResponseDto addWalkWish(String token, Long walkId) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Walk walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new RuntimeException("산책로를 찾을 수 없습니다."));

        if (walkWishRepository.existsByUserAndWalk(user, walk)) {
            throw new RuntimeException("이미 위시리스트에 추가된 산책로입니다.");
        }

        WalkWish walkWish = new WalkWish(user, walk, true);
        walkWishRepository.save(walkWish);

        return WalkWishResponseDto.builder()
                .status(200)
                .success(true)
                .message("위시리스트에 추가했습니다.")
                .build();
    }

    public WalkWishResponseDto cancelWalkWish(String token, Long walkId) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Walk walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new RuntimeException("산책로를 찾을 수 없습니다."));

        WalkWish walkWish = walkWishRepository.findByUserAndWalk(user, walk)
                .orElseThrow(() -> new RuntimeException("위시리스트에 해당 산책로가 없습니다."));

        walkWishRepository.delete(walkWish);

        return WalkWishResponseDto.builder()
                .status(200)
                .success(true)
                .message("위시리스트에서 제거했습니다.")
                .build();
    }
}
