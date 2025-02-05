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
@RequestMapping("/api/board/post")
@Tag(name = "Post API", description = "게시글 CRUD, 좋아요 API")
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    /**
     * 게시글 생성 API
     */
    @Operation(summary = "게시글 생성 API", description = "게시글을 작성해주세요(사용자 아이디 필수), 이미지는 필수입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping(value = "/users/{userId}",
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
    @GetMapping("/{postId}")
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
    @PutMapping(value = "/{postId}",
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
    @DeleteMapping("/{postId}")
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
    @PostMapping("/{postId}/like")
    public ApiResponse<String> toggleLike(
            @PathVariable("postId") Long postId,
            @RequestParam("userId") Long userId
    ) {
        boolean liked = postLikeService.toggleLike(postId, userId);
        return ApiResponse.onSuccess(liked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.");
    }

}
