package DC_square.spring.service;

import DC_square.spring.domain.entity.Event;
import DC_square.spring.repository.EventRepository;
import DC_square.spring.web.dto.response.EventResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    public List<EventResponseDto> getEvents() {
        LocalDate today = LocalDate.now();

        // 현재 진행 중이거나 시작하지 않은 이벤트를 조회
        List<Event> events = eventRepository.findByEndDateGreaterThanEqualOrderByStartDateAsc(today);

        //event 엔티티 리스트를 EventResponseDto 리스트로 변환
        return events.stream()
                .map(EventResponseDto::from)
                .collect(Collectors.toList());
    }
}