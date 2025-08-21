package DC_square.spring.service.dday;

import DC_square.spring.domain.entity.Dday;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.DdayType;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.dday.DdayRepository;
import DC_square.spring.web.dto.request.dday.DdayRequestDto;
import DC_square.spring.web.dto.request.dday.DdayUpdateRequestDto;
import DC_square.spring.web.dto.response.dday.DdayResponseDto;
import DC_square.spring.service.notification.NotificationService;
import DC_square.spring.domain.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DdayService {
    private final DdayRepository ddayRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public DdayResponseDto createDday(Long userId, DdayRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Dday dday = request.toEntity(user);
        dday.setType(DdayType.CUSTOM);
        dday.setDefaultImageUrl();
        dday.setIsAlarm(false);

        Dday savedDday = ddayRepository.save(dday);
        return DdayResponseDto.from(savedDday);
    }

    @Transactional
    public List<DdayResponseDto> getDdaysByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<Dday> ddays = ddayRepository.findAllByUserOrderByDayAsc(user);
        LocalDate today = LocalDate.now();

        List<Dday> updatedDdays = new ArrayList<>();

        for (Dday dday : ddays) {
            if (dday.getTerm() != null &&
                    dday.getTerm() > 0 &&
                    dday.getDay().isBefore(today)) {

                LocalDate lastDday = dday.getDay();
                int maxIterations = 52;  // 최대 1년 (52주)
                int iterations = 0;

                while (lastDday.isBefore(today) && iterations < maxIterations) {
                    lastDday = lastDday.plusWeeks(dday.getTerm());
                    iterations++;
                }

                dday.setDay(lastDday);
                updatedDdays.add(dday);
            }
        }

        if (!updatedDdays.isEmpty()) {
            ddayRepository.saveAll(updatedDdays);
        }

        return ddays.stream()
                .map(DdayResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public DdayResponseDto updateDday(Long userId, Long ddayId, DdayUpdateRequestDto request) {
        Dday dday = ddayRepository.findById(ddayId)
                .orElseThrow(() -> new RuntimeException("D-day를 찾을 수 없습니다."));

        if (!dday.getUser().getId().equals(userId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

//        if (request.getTitle() != null) {
//            dday.setTitle(request.getTitle());
//        }

        if (request.getDay() != null) {
            dday.setDay(request.parseDay());
        }
        if (request.getTerm() != null) {
            dday.setTerm(request.getTerm());
        }
        if (request.getIsAlarm() != null) {
            dday.setIsAlarm(request.getIsAlarm());
        }

        Dday updatedDday = ddayRepository.save(dday);
        return DdayResponseDto.from(updatedDday);
    }

    @Transactional
    public void deleteDday(Long userId, Long ddayId) {
        Dday dday = ddayRepository.findById(ddayId)
                .orElseThrow(() -> new RuntimeException("D-day를 찾을 수 없습니다."));

        if (!dday.getUser().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        ddayRepository.delete(dday);
    }

    public List<Dday> getDdayEntitiesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return ddayRepository.findAllByUserOrderByDayAsc(user);
    }

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendDdayNotification() {
        LocalDate today = LocalDate.now();

        List<Dday> ddaysWithAlarm = ddayRepository.findAllByIsAlarmTrue();
        log.info("알림 켜진 디데이 개수: {}", ddaysWithAlarm.size());

        for (Dday dday : ddaysWithAlarm) {
            long daysRemaining = ChronoUnit.DAYS.between(today, dday.getDay());
            log.info("D-day: {}, 남은 일수: {}", dday.getTitle(), daysRemaining);

            if (daysRemaining >= 0 && daysRemaining <= 3) {
                String title = "주기 알림";
                String body = daysRemaining == 0
                        ? dday.getTitle() + " 디데이입니다!"
                        : dday.getTitle() + "까지 " + daysRemaining + "일 남았습니다.";

                try {
                    notificationService.sendNotificationAndSave(
                            NotificationType.DDAY,
                            dday.getUser(),
                            title,
                            body
                    );
                    log.info("D-day 알림 전송 완료: {} ({}일 남음)", dday.getTitle(), daysRemaining);
                } catch (Exception e) {
                    log.error("D-day 알림 전송 실패: {}, 오류: {}", dday.getTitle(), e.getMessage());
                }
            }
        }
    }
}