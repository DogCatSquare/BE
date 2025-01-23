package DC_square.spring.web.controller.community;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.community.BoardService;
import DC_square.spring.web.dto.request.community.BoardRequestDto;
import DC_square.spring.web.dto.response.community.BoardResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시판 생성 API
     */
    @Operation(summary = "게시판 생성 API",description = "제목, 내용, 키워드(리스트 형태로) 작성해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ApiResponse<BoardResponseDto> createBoard(
            @Valid @RequestPart("request") BoardRequestDto boardRequestDto,
            @RequestPart(value = "boardPicture") MultipartFile boardPicture
    ) {
        return ApiResponse.onSuccess(boardService.createdBoard(boardRequestDto, boardPicture));
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

}
