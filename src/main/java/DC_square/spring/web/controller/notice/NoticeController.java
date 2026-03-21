package DC_square.spring.web.controller.notice;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.notice.NoticeService;
import DC_square.spring.web.dto.response.notice.NoticeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
@Tag(name = "Notice API", description = "공지사항 조회 API(안드로이드에서 사용할 API)")
public class NoticeController {

  private final NoticeService noticeService;

  @Operation(summary = "공지사항 단건 조회")
  @GetMapping("/{noticeId}")
  public ApiResponse<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
    return ApiResponse.onSuccess(noticeService.getNotice(noticeId));
  }

  @Operation(summary = "공지사항 목록 조회")
  @GetMapping
  public ApiResponse<List<NoticeResponseDto>> getNoticeList() {
    return ApiResponse.onSuccess(noticeService.getNoticeList());
  }

}
