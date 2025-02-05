package DC_square.spring.service.community;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.community.Post;
import DC_square.spring.domain.entity.community.PostLike;
import DC_square.spring.repository.community.PostLikeRepository;
import DC_square.spring.repository.community.PostRepository;
import DC_square.spring.repository.community.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 좋아요 추가 및 취소
     */
    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            postLikeRepository.delete(existingLike.get());
            post.setLikeCount(post.getLikeCount() - 1);
            return false; // 좋아요 취소됨
        } else {
            // 좋아요 추가
            PostLike newLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
            return true; // 좋아요 추가됨
        }
    }
}
