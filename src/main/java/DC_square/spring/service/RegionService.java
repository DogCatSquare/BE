package DC_square.spring.service;

import DC_square.spring.domain.entity.Region;
import DC_square.spring.web.dto.request.RegionRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import DC_square.spring.repository.RegionRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    public Long creatRegion(RegionRequestDTO request) {
        Region region = Region.builder()
                .Do(request.getDo())
                .si(request.getSi())
                .gu(request.getGu())
                .build();

        return regionRepository.save(region).getId();
    }
}
