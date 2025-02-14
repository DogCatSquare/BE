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
import java.util.Optional;
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

        // 유튜브 영상 ID 추출
        String thumbnailUrl = null;
        if (postRequestDto.getVideo_URL() != null && !postRequestDto.getVideo_URL().isEmpty()) {
            String videoId = postRequestDto.getVideo_URL().substring(postRequestDto.getVideo_URL().length() - 11);
            thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        }


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
                .created_at(LocalDateTime.now())
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
                .username(savedPost.getUser().getNickname())
                .comment_count(savedPost.getCommentCount())
                .thumbnail_URL(thumbnailUrl)
                .profileImage_URL(user.getProfileImageUrl())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 특정 게시글 조회 API(한개 조회)
     */
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        // 유튜브 영상 ID 추출
        String thumbnailUrl = null;
        if (post.getVideo_URL() != null && !post.getVideo_URL().isEmpty()) {
            String videoId = post.getVideo_URL().substring(post.getVideo_URL().length() - 11);
            thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        }


        return PostResponseDto.builder()
                .id(post.getId())
                .board(post.getBoard().getBoardName())
                .title(post.getTitle())
                .content(post.getContent())
                .content(post.getContent())
                .video_URL(post.getVideo_URL())
                .username(post.getUser().getNickname())
                .thumbnail_URL(thumbnailUrl)
                .profileImage_URL(post.getUser().getProfileImageUrl())
                .images(post.getCommunityImages())
                .like_count(post.getLikeCount())
                .thumbnail_URL(post.getVideo_URL() + "/0.jpg")
                .comment_count(post.getCommentCount())
                .createdAt(post.getCreated_at())
                .build();
    }

    /**
     * 게시판 id로 특정 게시판에 있는 게시글들 조회
     */
    public List<PostResponseDto> getPosts(Long boardId) {

        // 게시판 ID가 유효한지 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판 ID가 존재하지 않습니다."));

        // 게시판 ID로 게시글들 조회
        List<Post> posts = postRepository.findByBoardId(boardId);


        // 게시글이 없으면 예외 처리
        if (posts.isEmpty()) {
            throw new IllegalArgumentException("해당 게시판에 게시글이 존재하지 않습니다.");
        }

        // PostResponseDto로 변환하여 반환
        return posts.stream()
                .map(post -> {
                    // 유튜브 영상 ID 추출 (썸네일 URL 생성)
                    String thumbnailUrl = null;
                    if (post.getVideo_URL() != null && !post.getVideo_URL().isEmpty()) {
                        String videoId = post.getVideo_URL().substring(post.getVideo_URL().length() - 11);
                        thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
                    }

                    return PostResponseDto.builder()
                            .id(post.getId())
                            .board(post.getBoard().getBoardName())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .video_URL(post.getVideo_URL())
                            .thumbnail_URL(thumbnailUrl)
                            .username(post.getUser().getNickname())
                            .profileImage_URL(post.getUser().getProfileImageUrl())
                            .images(post.getCommunityImages())
                            .like_count(post.getLikeCount())
                            .comment_count(post.getCommentCount())
                            .createdAt(post.getCreated_at()) // 게시글 생성일
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 작성한 모든 게시글 조회
     */
    public List<PostResponseDto> getPostsByUser(Long userId) {
        //사용자가 존재하는지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        //사용자가 작성한 게시글들 조회
        List<Post> posts = postRepository.findByUserId(userId);

        // 게시글이 없으면 예외 처리
        if (posts.isEmpty()) {
            throw new RuntimeException("사용자가 작성한 게시글이 존재하지 않습니다.");
        }

        // 게시글들을 PostResponseDto로 변환하여 반환
        return posts.stream()
                .map(post -> {
                    // 유튜브 영상 ID 추출 (썸네일 URL 생성)
                    String thumbnailUrl = null;
                    if (post.getVideo_URL() != null && !post.getVideo_URL().isEmpty()) {
                        String videoId = post.getVideo_URL().substring(post.getVideo_URL().length() - 11);
                        thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
                    }

                    return PostResponseDto.builder()
                            .id(post.getId())
                            .board(post.getBoard().getBoardName()) // 게시판 이름
                            .title(post.getTitle())
                            .content(post.getContent())
                            .video_URL(post.getVideo_URL())
                            .thumbnail_URL(thumbnailUrl)
                            .username(post.getUser().getNickname())
                            .profileImage_URL(post.getUser().getProfileImageUrl())
                            .images(post.getCommunityImages())
                            .like_count(post.getLikeCount())
                            .comment_count(post.getCommentCount())
                            .createdAt(post.getCreated_at()) // 게시글 생성일
                            .build();
                })
                .collect(Collectors.toList());
    }




    /**
     * 게시글 수정 API
     */
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, List<MultipartFile> newImages) {
        // 기존 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        // 유튜브 영상 ID 추출
        String thumbnailUrl = null;
        if (postRequestDto.getVideo_URL() != null && !postRequestDto.getVideo_URL().isEmpty()) {
            String videoId = postRequestDto.getVideo_URL().substring(postRequestDto.getVideo_URL().length() - 11);
            thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        }

        // 제목과 내용 수정
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setVideo_URL(postRequestDto.getVideo_URL());
        post.setCreated_at(LocalDateTime.now());


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
                .thumbnail_URL(thumbnailUrl)
                .username(savedPost.getUser().getNickname())
                .images(savedPost.getCommunityImages()) // 수정된 이미지 목록
                .like_count(savedPost.getLikeCount())
                .profileImage_URL(savedPost.getUser().getProfileImageUrl())
                .comment_count(savedPost.getCommentCount())
                .createdAt(savedPost.getCreated_at()) // 수정된 날짜 그대로 반환
                .build();
    }

    /**
     * 게시글 삭제 API
     */
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    /**
     * 인기 게시글 목록을 가져옵니다.
     * 좋아요 수가 10개 이상인 게시글만 가져오고, 좋아요 수 내림차순으로 정렬합니다.
     */
    public List<PostResponseDto> getPopularPosts() {
        List<Post> popularPosts = postRepository.findByLikeCountGreaterThanEqualOrderByLikeCountDesc(10);

        return popularPosts.stream()
                .map(post -> PostResponseDto.builder()
                        .id(post.getId())
                        .board(post.getBoard().getBoardName()) // 게시판 이름
                        .username(post.getUser().getNickname()) // 사용자 이름
                        .title(post.getTitle())
                        .content(post.getContent())
                        .video_URL(post.getVideo_URL())
                        .thumbnail_URL(post.getVideo_URL()) // 비디오 썸네일 추가 (예시)
                        .profileImage_URL(post.getUser().getProfileImageUrl()) // 사용자 프로필 이미지
                        .images(post.getCommunityImages()) // 게시글 이미지들
                        .like_count(post.getLikeCount())
                        .comment_count(post.getCommentCount())
                        .createdAt(post.getCreated_at())
                        .build())
                .collect(Collectors.toList());
    }
}
