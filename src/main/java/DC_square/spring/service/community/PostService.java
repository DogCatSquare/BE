package DC_square.spring.service.community;

import DC_square.spring.config.S3.AmazonS3Manager;
import DC_square.spring.config.S3.Uuid;
import DC_square.spring.config.S3.UuidRepository;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.community.Board;
import DC_square.spring.domain.entity.community.Post;
import DC_square.spring.repository.community.BoardRepository;
import DC_square.spring.repository.community.PostRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.request.community.PostRequestDto;
import DC_square.spring.web.dto.response.community.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;


    /**
    게시글 생성 API
     */
    public PostResponseDto createPost(List<MultipartFile> images,PostRequestDto postRequestDto, Long userId) {

        // images가 null인 경우 빈 리스트로 초기화
        List<String> imageUrls = (images != null) ? images.stream()
                .map(image -> {
                    String uuid = UUID.randomUUID().toString();
                    Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
                    return s3Manager.uploadFile(s3Manager.generateCommunity(savedUuid), image);
                })
                .collect(Collectors.toList()) : new ArrayList<>();


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자 조회할 수 없습니다."));

        Board findBoard = boardRepository.findById(postRequestDto.getBoardId())
                .orElseThrow(() -> new RuntimeException("해당 게시판이 없습니다."));


        //Post 엔티티
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .likeCount(0) //초기 좋아요 수
                .commentCount(0) //초기 댓글 수
                .communityImages(imageUrls)
                .user(user)
                .communityImages(imageUrls)
                .video_URL(postRequestDto.getVideo_URL())
                .board(findBoard)
                .build();

        //저장
        Post savedPost = postRepository.save(post);

        //PostResponseDto로 반환
        return PostResponseDto.builder()
                .id(savedPost.getId())
                .board(savedPost.getBoard().getBoardName())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .video_URL(savedPost.getVideo_URL())
                .images(savedPost.getCommunityImages())
                .like_count(savedPost.getLikeCount())
                .comment_count(savedPost.getCommentCount())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 특정 게시글 조회 API(한개 조회)
     */
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        return PostResponseDto.builder()
                .id(post.getId())
                .board(post.getBoard().getBoardName())
                .title(post.getTitle())
                .content(post.getContent())
                .content(post.getContent())
                .video_URL(post.getVideo_URL())
                .images(post.getCommunityImages())
                .like_count(post.getLikeCount())
                .comment_count(post.getCommentCount())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 게시글 수정 API
     */
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, List<MultipartFile> newImages) {
        // 기존 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        // 제목과 내용 수정
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setVideo_URL(postRequestDto.getVideo_URL());

        // 이미지 수정 여부 체크
        if (newImages != null && !newImages.isEmpty()) {
            // 기존 이미지 삭제 (기존 이미지를 모두 삭제)
            post.getCommunityImages().clear();

            // 새로운 이미지 업로드
            List<String> newImageUrls = newImages.stream()
                    .map(image -> {
                        String uuid = UUID.randomUUID().toString();
                        Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
                        return s3Manager.uploadFile(s3Manager.generateCommunity(savedUuid), image);
                    })
                    .collect(Collectors.toList());

            // 새로운 이미지 목록으로 교체
            post.setCommunityImages(newImageUrls);
        }

        // 게시글 저장 - DB에 반영
        Post savedPost = postRepository.save(post);

        // 수정된 게시글 응답 DTO 반환
        return PostResponseDto.builder()
                .id(savedPost.getId())
                .board(savedPost.getBoard().getBoardName())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .video_URL(savedPost.getVideo_URL())
                .images(savedPost.getCommunityImages()) // 수정된 이미지 목록
                .like_count(savedPost.getLikeCount())
                .comment_count(savedPost.getCommentCount())
                .createdAt(LocalDateTime.now()) // 수정된 날짜 그대로 반환
                .build();
    }

    /**
     * 게시글 삭제 API
     */
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

}
