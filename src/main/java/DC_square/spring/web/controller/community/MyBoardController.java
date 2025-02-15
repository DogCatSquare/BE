package DC_square.spring.web.controller.community;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.service.community.MyBoardService;
import DC_square.spring.web.dto.response.community.MyBoardResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/myboard")
@Tag(name = "MyBoard API", description = "마이게시판 API")
public class MyBoardController {

    private final MyBoardService myBoardService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 마이게시판 추가 API
     */
    @Operation(summary = "마이게시판 추가 API", description = "boardId를 입력하여 마이게시판에 추가합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @PostMapping
    public ApiResponse<MyBoardResponseDto> addMyBoard(
            @RequestParam Long boardId,
            HttpServletRequest request
    ) {
        String token = jwtTokenProvider.resolveToken(request);
        return ApiResponse.onSuccess(myBoardService.addMyBoard(boardId, token));
    }

    /**
     * 마이게시판 목록 API
     */
    @Operation(summary = "마이게시판 조회 API", description = "마이게시판 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping
    public ApiResponse<List<MyBoardResponseDto>> getMyBoards(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return ApiResponse.onSuccess(myBoardService.getMyBoards(token));
    }

    /**
     * 마이게시판 삭제 API
     */
    @Operation(summary = "마이게시판 삭제 API", description = "마이게시판에서 특정 게시판을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @DeleteMapping("/{myBoardId}")
    public ApiResponse<Void> removeMyBoard(@PathVariable Long myBoardId, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        myBoardService.removeMyBoard(myBoardId, token);
        return ApiResponse.onSuccess(null);
    }

    /**
     * 홈 화면 조회 API (기본 게시판 + 마이게시판)
     */
    @Operation(summary = "홈 화면 조회 API", description = "기본 게시판과 마이게시판을 포함한 홈 화면 게시판 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @GetMapping("/home")
    public ApiResponse<List<String>> getHomeBoards(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return ApiResponse.onSuccess(myBoardService.getHomeBoards(token));
    }
}