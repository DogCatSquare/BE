package DC_square.spring.web.controller.community;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.community.PostLikeService;
import DC_square.spring.service.community.PostService;
import DC_square.spring.web.dto.request.community.PostRequestDto;
import DC_square.spring.web.dto.response.community.PostResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Tag(name = "Post API", description = "게시글 CRUD, 좋아요 API")
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    /**
     * 게시글 생성 API
     */
    @Operation(summary = "게시글 생성 API", description = "게시글을 작성해주세요(사용자 아이디 필수), 이미지는 선택입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping(value = "/post/users/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // consumes에서 이미지 타입을 제거하고 multipart/form-data만 사용
    public ApiResponse<PostResponseDto> createPost(
            @Valid @RequestPart("request") PostRequestDto postRequestDto,
            @PathVariable("userId") Long userId,
            @RequestPart(value = "communityImages", required = false) List<MultipartFile> images
    ) {
        return ApiResponse.onSuccess(postService.createPost(images, postRequestDto, userId));
    }

    /**
     * 특정 게시글 조회 API
     */
    @Operation(summary = "특정 게시글 조회 API", description = "게시글 아이디를 입력해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/post/{postId}")
    public ApiResponse<PostResponseDto> getPost(@PathVariable Long postId) {
        return ApiResponse.onSuccess(postService.getPost(postId));
    }

    /**
     * 게시글 수정 API
     */
    @Operation(summary = "게시글 수정 API", description = "게시글 아이디를 입력해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PutMapping(value = "/post/{postId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // consumes에서 이미지 타입을 제거하고 multipart/form-data만 사용
    public ApiResponse<PostResponseDto> updatePost(
            @Valid @RequestPart("request") PostRequestDto postRequestDto,
            @PathVariable("postId") Long postId,
            @RequestPart(value = "communityImages", required = false) List<MultipartFile> newImages
    ) {
        return ApiResponse.onSuccess(postService.updatePost(postId, postRequestDto, newImages));
    }

    /**
     * 게시글 삭제 API
     */
    @Operation(summary = "게시글 삭제 API", description = "게시글 아이디를 입력해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @DeleteMapping("/post/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ApiResponse.onSuccess(null);
    }

    /**
     * 게시글 좋아요 추가 및 취소 API
     */
    @Operation(summary = "게시글 좋아요 추가/취소 API", description = "게시글에 좋아요를 추가하거나 취소할 수 있습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping("/post/{postId}/like")
    public ApiResponse<String> toggleLike(
            @PathVariable("postId") Long postId,
            @RequestParam("userId") Long userId
    ) {
        boolean liked = postLikeService.toggleLike(postId, userId);
        return ApiResponse.onSuccess(liked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.");
    }

    /**
     * 특정 게시판에 있는 게시글들 조회
     */
    @Operation(summary = "특정 게시판에 있는 게시글들 조회 API",description = "게시판 아이디를 입력해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/{boardId}/posts")
    public ApiResponse<List<PostResponseDto>> getPostsByBoard(@PathVariable Long boardId) {
        return ApiResponse.onSuccess(postService.getPosts(boardId));
    }

    /**
     * 인기 게시글 목록 조회
     */
    @Operation(summary = "인기 게시글 목록 조회 API",description = "인기 게시글 목록 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/posts/popular")
    public ApiResponse<List<PostResponseDto>> getPopularPosts() {
        List<PostResponseDto> popularPosts = postService.getPopularPosts();
        return ApiResponse.onSuccess(popularPosts);
    }

    /**
     * 사용자가 작성한 게시글 모두 조회
     */
    @Operation(summary = "사용자가 작성한 게시글 모두 조회",description = "사용자가 작성한 게시글 목록 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/posts/user/{userId}")
    public ApiResponse<List<PostResponseDto>> getPostsByUser(@PathVariable Long userId) {
        List<PostResponseDto> userPosts = postService.getPostsByUser(userId);
        return ApiResponse.onSuccess(userPosts);
    }

    /**
     * 하하하
     */



}
