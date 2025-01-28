package DC_square.spring.web.controller.community;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.community.BoardService;
import DC_square.spring.web.dto.request.community.BoardRequestDto;
import DC_square.spring.web.dto.response.community.BoardResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

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
        return ApiResponse.onSuccess(boardService.getBoard(boardId));
    }

    /**
     * 게시판 수정 API
     */
    @Operation(summary = "게시판 수정 API",description = "게시판 아이디를 입력해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @PutMapping("/{boardId}")
    public ApiResponse<BoardResponseDto> updateBoard(@PathVariable Long boardId, @Valid @RequestBody BoardRequestDto boardRequestDto) {
        return ApiResponse.onSuccess(boardService.updateBoard(boardId,boardRequestDto));
    }

    /**
     * 게시판 삭제 API
     */
    @Operation(summary = "게시판 삭제 API",description = "게시판 아이디를 입력해주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공")
    })
    @DeleteMapping("/{boardId}")
    public ApiResponse<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ApiResponse.onSuccess(null); //성공 응답 반환
    }




}
