package DC_square.spring.service;

import DC_square.spring.domain.entity.Region;
import DC_square.spring.domain.entity.region.City;
import DC_square.spring.domain.entity.region.District;
import DC_square.spring.domain.entity.region.Province;
import DC_square.spring.repository.region.CityRepository;
import DC_square.spring.repository.region.DistrictRepository;
import DC_square.spring.repository.region.ProvinceRepository;
import DC_square.spring.web.dto.request.RegionRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import DC_square.spring.repository.RegionRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RegionService {
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;

    public District findOrCreateDistrict(String provinceName, String cityName, String districtName) {
        // 1. Province 찾기 - 정확한 이름으로 찾기
        Province province = provinceRepository.findByName(provinceName)
                .orElseGet(() -> {
                    log.info("Creating new province: {}", provinceName);
                    return provinceRepository.save(Province.builder()
                            .name(provinceName)
                            .build());
                });

        // 2. City 찾기 - 정확한 이름으로 찾기
        City city = cityRepository.findByNameAndProvince(cityName, province)
                .orElseGet(() -> {
                    log.info("Creating new city: {} in province: {}", cityName, provinceName);
                    return cityRepository.save(City.builder()
                            .name(cityName)
                            .province(province)
                            .build());
                });

        // 3. District 찾기 - 정확한 이름으로 찾기
        return districtRepository.findByNameAndCity(districtName, city)
                .orElseGet(() -> {
                    log.info("Creating new district: {} in city: {}", districtName, cityName);
                    return districtRepository.save(District.builder()
                            .name(districtName)
                            .city(city)
                            .build());
                });
    }
}