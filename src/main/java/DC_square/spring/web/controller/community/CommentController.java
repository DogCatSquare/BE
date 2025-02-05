package DC_square.spring.web.controller.community;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.community.CommentService;
import DC_square.spring.web.dto.request.community.CommentRequestDto;
import DC_square.spring.web.dto.response.community.CommentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments/posts/{postId}")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 API
     */
    @Operation(summary = "댓글 생성 API", description = "게시글id, 유저id를 꼭 넣어주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @PostMapping("/users/{userId}")
    public ApiResponse<CommentResponseDto> createComment(@PathVariable Long postId,
                                            @PathVariable Long userId,
                                            @Valid @RequestBody CommentRequestDto commentRequestDto){
        return ApiResponse.onSuccess(commentService.createComment(postId, userId, commentRequestDto));
    }

    /**
     * 특정 게시물의 댓글 조회 API
     */
    @Operation(summary = "댓글 조회 API", description = "게시글id를 꼭 넣어주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @GetMapping
    public ApiResponse<List<CommentResponseDto>> getComments(@PathVariable Long postId){
        return ApiResponse.onSuccess(commentService.getComments(postId));
    }
}
