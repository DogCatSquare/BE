package DC_square.spring.web.controller.community;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.community.BoardService;
import DC_square.spring.service.community.PostService;
import DC_square.spring.web.dto.request.community.BoardRequestDto;
import DC_square.spring.web.dto.request.community.PostRequestDto;
import DC_square.spring.web.dto.response.community.BoardResponseDto;
import DC_square.spring.web.dto.response.community.PostResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final PostService postService;

    /**
     * 게시판 생성 API
     */
    @Operation(summary = "게시판 생성 API",description = "게시판 이름, 내용, 키워드(리스트 형태로) 작성해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @PostMapping
    public ApiResponse<BoardResponseDto> createBoard(
            @Valid BoardRequestDto boardRequestDto
    ) {
        return ApiResponse.onSuccess(boardService.createdBoard(boardRequestDto));
    }

    /**
     * 게시판 조회 API
     */
    @Operation(summary = "게시판 조회 API",description = "게시판 아이디를 입력해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponseDto> getBoard(@PathVariable Long boardId) {
        BoardResponseDto board = boardService.getBoard(boardId);
        return ApiResponse.onSuccess(board);
    }

    /**
     * 게시글 생성 API
     */
    @Operation(summary = "게시글 생성 API",description = "게시글을 작성해주세요(사용자 아이디 필수)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @PostMapping(value = "/post/users/{userId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE
    })
    public ApiResponse<PostResponseDto> createPost(
            @Valid @RequestPart("request") PostRequestDto postRequestDto,
            @PathVariable("userId") Long userId,
            @RequestPart(value = "communityImages") List<MultipartFile> images
    ) {
        return ApiResponse.onSuccess(postService.createPost(images, postRequestDto, userId));
    }

    /**
     * 특정 게시글 조회 API
     */
    @Operation(summary = "특정 게시글 조회 API", description = "게시글 아이디를 입력해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @GetMapping("/post/{postId}")
    public ApiResponse<PostResponseDto> getPost(@PathVariable Long postId) {
        return ApiResponse.onSuccess(postService.getPost(postId));
    }


}
