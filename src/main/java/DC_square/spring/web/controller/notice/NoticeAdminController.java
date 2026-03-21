package DC_square.spring.web.controller.notice;

import DC_square.spring.service.notice.NoticeService;
import DC_square.spring.web.dto.request.notice.NoticeRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Tag(name = "Notice Admin API", description = "어드민 전용 공지사항 API")
public class NoticeAdminController {

  private final NoticeService noticeService;

  // 로그인 폼 페이지
  @GetMapping("/admin/login")
  public String loginForm() {
    return "notice/login";
  }

  // 공지 작성 폼 페이지 열기
  @GetMapping("/admin/notices/new")
  public String noticeForm(Model model) {
    model.addAttribute("noticeRequestDto", new NoticeRequestDto());
    return "notice/notice-form";
  }

  // 공지 작성
  @PostMapping("/admin/notices")
  public String createNotice(@Valid @ModelAttribute NoticeRequestDto requestDto) {
    noticeService.createNotice(requestDto);
    return "redirect:/admin/notices/list";
  }

  @GetMapping("/admin/notices/list")
  public String noticeList(Model model) {
    model.addAttribute("notices", noticeService.getNoticeList());
    return "notice/notice-list";
  }

  // 공지 상세 조회
  @GetMapping("/admin/notices/{noticeId}")
  public String noticeDetail(@PathVariable Long noticeId, Model model) {
    model.addAttribute("notice", noticeService.getNotice(noticeId));
    return "notice/notice-detail";
  }


}
