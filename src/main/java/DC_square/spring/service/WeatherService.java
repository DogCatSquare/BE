package DC_square.spring.service;

import DC_square.spring.constant.WeatherConstants;
import DC_square.spring.domain.entity.Dday;
import DC_square.spring.domain.entity.Pet;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.WeatherStatus;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.dday.DdayRepository;
import DC_square.spring.repository.PetRepository;
import DC_square.spring.web.dto.response.WeatherResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    @Value("${weather.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final DdayRepository ddayRepository;

    @PostConstruct
    public void testWeatherApiConnection() {
        try {
            String testUrl = String.format(
                    WeatherConstants.WEATHER_FORECAST_URL +
                            "?pageNo=1" +  // pageNo를 가장 먼저 배치
                            "&base_date=%s" +
                            "&base_time=0500" +
                            "&authKey=%s" +  //  authKey 위치 조정
                            "&numOfRows=1" +
                            "&nx=55" +
                            "&ny=127" +
                            "&dataType=JSON", //  dataType을 마지막에 배치
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    URLEncoder.encode(serviceKey, StandardCharsets.UTF_8) // ✅ 올바르게 인코딩
            );

            ResponseEntity<String> response = restTemplate.getForEntity(testUrl, String.class);
            log.info("Weather API Test Response: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Weather API Connection Test Failed", e);
        }
    }

    public WeatherResponseDto getCurrentWeather(Long userId) {

        try {
            // 1. 사용자와 반려동물 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            Pet pet = petRepository.findByUser(user);
            if (pet == null) {
                throw new RuntimeException("반려동물 정보를 찾을 수 없습니다.");
            }

            // 2. API 호출 정보 준비
            LocalDateTime now = LocalDateTime.now();
            String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        //  String baseTime = String.format("%02d00", (now.getHour() / 3) * 3);
            String baseTime = String.format("%02d00", ((now.getHour() >= 23 ? 0 : now.getHour()) / 3) * 3);

            // 3. API 호출
            String url = String.format(
                    WeatherConstants.WEATHER_FORECAST_URL +
                            "?pageNo=%d" +
                            "&base_date=%s" +
                            "&base_time=%s" +
                            "&authKey=%s" +
                            "&numOfRows=%d" +
                            "&nx=%d" +
                            "&ny=%d" +
                            "&dataType=JSON",
                    1,  // pageNo
                    baseDate,
                   // baseTime,
                    "0200",
                    URLEncoder.encode(serviceKey, StandardCharsets.UTF_8), // authKey  인코딩
                    260,  // numOfRows
                    user.getDistrict().getCity().getGrid_X(),
                    user.getDistrict().getCity().getGrid_Y()
            );

            log.info("Weather API Request URL: {}", url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("Weather API Response Body: {}", response.getBody());

            String responseBody = response.getBody();
            if (responseBody == null || !responseBody.trim().startsWith("{")) {
                log.error("Invalid API response: {}", responseBody);
                throw new RuntimeException("올바른 JSON 응답이 아닙니다.");
            }

            // JSON 파싱
            JSONArray items = new JSONObject(response.getBody())
                    .getJSONObject("response")
                    .getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONArray("item");

            // 4. 날씨 데이터 추출
            String tmp = null, pty = null, sky = null, wsd = null,tmn=null,tmx=null;
            int currentHour = now.getHour();

// 가장 최근의 예보 시간을 찾기 위한 변수들
            int closestHour = 0;
            int minTimeDiff = 24; // 시간 차이 초기값

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String category = item.getString("category");
                String value = item.getString("fcstValue");
                String fcstTime = item.getString("fcstTime");

                // 예보 시간을 정수로 변환 (예: "1500" -> 15)
                int forecastHour = Integer.parseInt(fcstTime.substring(0, 2));

                switch (category) {
                    case WeatherConstants.TEMPERATURE:
                        // 현재 시간과 가장 가까운 이전 예보 시간 찾기
                        int timeDiff = (currentHour - forecastHour + 24) % 24;
                        if (timeDiff < minTimeDiff && timeDiff >= 0) {
                            minTimeDiff = timeDiff;
                            closestHour = forecastHour;
                            tmp = value;
                        }
                        break;
                    case WeatherConstants.MAX_TEMP:
                        tmx = value;
                        break;
                    case WeatherConstants.MIN_TEMP:
                        tmn = value;
                        break;
                    case WeatherConstants.RAIN_TYPE:
                        if (forecastHour == closestHour) pty = value;
                        break;
                    case WeatherConstants.SKY:
                        if (forecastHour == closestHour) sky = value;
                        break;
                    case WeatherConstants.WIND_SPEED:
                        if (forecastHour == closestHour) wsd = value;
                        break;
                }
            }

            // 5. 날씨 상태 결정
            WeatherStatus status = WeatherStatus.fromWeatherData(pty, sky, Double.parseDouble(wsd));

            // 6. 가장 가까운 D-day 찾기 (날씨가 흐린 경우)
             //원래 코드
            Dday nearestDday = status == WeatherStatus.CLOUDY ? findNearestDday(user) : null;

//            // 맑음일 때 테스트
//            Dday nearestDday = status == WeatherStatus.SUNNY ? findNearestDday(user) : null;


            // 7. 응답 생성
            return WeatherResponseDto.from(
                    status,
                    pet.getDogCat(),
                    user.getDistrict().getName(), //3 단계만
                    tmp,
                    tmx,
                    tmn,
                    nearestDday
            );

        } catch (Exception e) {
            log.error("날씨 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("날씨 정보를 조회할 수 없습니다: " + e.getMessage());
        }
    }

    private Dday findNearestDday(User user) {
        List<Dday> ddays = ddayRepository.findAllByUserOrderByDayAsc(user);
        log.info("User's Ddays: {}", ddays);
        LocalDate today = LocalDate.now();

        return ddays.stream()
                .filter(dday -> {
                    String title = dday.getTitle();
                    // DDAY중 제목이 해당하는 것만 필터
                    boolean isValidTitle = title.equals("사료 구매") ||
                            title.equals("패드/모래 구매") ||
                            title.equals("병원 방문일");
                    // 오늘 이후의 날짜만 필터
                    boolean isFutureDate = !dday.getDay().isBefore(today);

                    // 필터링 과정 로그
                    log.info("Checking Dday - Title: {}, Date: {}, isValidTitle: {}, isFutureDate: {}",
                            title, dday.getDay(), isValidTitle, isFutureDate);

                    return isValidTitle && isFutureDate;
                })
                .min(Comparator.comparing(Dday::getDay)) // 가장 가까운 날짜 선택
                .orElse(null);
    }
}