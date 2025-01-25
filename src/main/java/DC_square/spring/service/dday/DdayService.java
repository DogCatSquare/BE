package DC_square.spring.service.dday;

import DC_square.spring.domain.entity.Dday;
import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.dday.DdayRepository;
import DC_square.spring.web.dto.request.dday.DdayRequestDto;
import DC_square.spring.web.dto.response.dday.DdayResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DdayService {
    private final DdayRepository ddayRepository;
    private final UserRepository userRepository;

    @Transactional
    public DdayResponseDto createDday(Long userId, DdayRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Dday dday = request.toEntity(user);
        Dday savedDday = ddayRepository.save(dday);

        return DdayResponseDto.from(savedDday);
    }

    public List<DdayResponseDto> getDdaysByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return ddayRepository.findAllByUserOrderByDayAsc(user)
                .stream()
                .map(DdayResponseDto::from)
                .collect(Collectors.toList());
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
}