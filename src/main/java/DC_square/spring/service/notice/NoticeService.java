package DC_square.spring.service.notice;

import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.notice.Notice;
import DC_square.spring.domain.enums.NotificationType;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.notice.NoticeRepository;
import DC_square.spring.service.notification.NotificationService;
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
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  @Transactional
  public NoticeResponseDto createNotice(NoticeRequestDto request) {
    Notice notice = Notice.create(request.getTitle(), request.getContent());
    NoticeResponseDto response = NoticeResponseDto.from(noticeRepository.save(notice));

    String message = "[공지] " + notice.getTitle();
    List<User> allUsers = userRepository.findAll();
    for (User user : allUsers) {
      notificationService.sendNotificationAndSave(NotificationType.NOTICE, user, message, notice.getId());
    }

    return response;
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

  @Transactional
  public NoticeResponseDto updateNotice(Long noticeId, NoticeRequestDto request) {
    Notice notice = noticeRepository.findById(noticeId)
        .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
    notice.update(request.getTitle(), request.getContent());
    return NoticeResponseDto.from(notice);
  }

  @Transactional
  public void deleteNotice(Long noticeId) {
    if (!noticeRepository.existsById(noticeId)) {
      throw new RuntimeException("공지사항을 찾을 수 없습니다.");
    }
    noticeRepository.deleteById(noticeId);
  }
}
