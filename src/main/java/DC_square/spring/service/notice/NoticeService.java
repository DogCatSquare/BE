package DC_square.spring.service.notice;

import DC_square.spring.domain.entity.notice.Notice;
import DC_square.spring.repository.notice.NoticeRepository;
import DC_square.spring.web.dto.request.notice.NoticeRequestDto;
import DC_square.spring.web.dto.response.notice.NoticeResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

  private final NoticeRepository noticeRepository;

  @Transactional
  public NoticeResponseDto createNotice(NoticeRequestDto request) {
    Notice notice = Notice.create(request.getTitle(), request.getContent());
    return NoticeResponseDto.from(noticeRepository.save(notice));
  }

  public NoticeResponseDto getNotice(Long noticeId) {
    Notice notice = noticeRepository.findById(noticeId)
        .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
    return NoticeResponseDto.from(notice);
  }

  public List<NoticeResponseDto> getNoticeList() {
    return noticeRepository.findAllByOrderByCreatedAtDesc().stream()
        .map(NoticeResponseDto::from)
        .collect(Collectors.toList());
  }
}
