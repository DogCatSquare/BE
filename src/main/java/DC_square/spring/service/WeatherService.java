package DC_square.spring.service;

import DC_square.spring.constant.WeatherConstants;
import DC_square.spring.domain.entity.Dday;
import DC_square.spring.domain.entity.Pet;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.WeatherStatus;
import DC_square.spring.repository.PetRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.repository.dday.DdayRepository;
import DC_square.spring.web.dto.response.WeatherResponseDto;
import jakarta.annotation.PostConstruct;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
      log.info("Pet type: {}", pet.getDogCat());

      // 2. API 호출 정보 준비
      LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));  // UTC 시간 문제 해결을 위해

      // 기본은 오늘 날짜(base_date)
      String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

      // 현재 시각 기준으로 API에서 사용 가능한 base_time 계산
      String baseTime = resolveBaseTime(now);

      // 새벽 02:10 이전이면 baseTime이 2300이 나오는데,
      //    이때는 "전날 23:00 발표"를 써야 하므로 날짜를 하루 빼야 함
      if ("2300".equals(baseTime) && now.getHour() < 2) {
        baseDate = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
      }

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
          baseTime,
          URLEncoder.encode(serviceKey, StandardCharsets.UTF_8), // authKey  인코딩
          300,  // numOfRows
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

      // 4. 날씨 데이터 추출 (항목별 가장 가까운 시간 찾기 방식)
      String tmp = null, tmn = null, tmx = null;
      String pty = null, sky = null, wsd = null, pop = null;

      int currentHour = now.getHour();
      int minDiffTMP = 24, minDiffPTY = 24, minDiffSKY = 24, minDiffPOP = 24, minDiffWSD = 24;

      for (int i = 0; i < items.length(); i++) {
        JSONObject item = items.getJSONObject(i);
        String category = item.getString("category");
        String value = item.getString("fcstValue");
        String fcstTime = item.getString("fcstTime");

        int forecastHour = Integer.parseInt(fcstTime.substring(0, 2));
        int timeDiff = (currentHour - forecastHour + 24) % 24;

        switch (category) {
          case WeatherConstants.TEMPERATURE:
            if (timeDiff < minDiffTMP) {
              minDiffTMP = timeDiff;
              tmp = value;
            }
            break;
          case WeatherConstants.MAX_TEMP:
            tmx = value;  // 하루 1~2회 제공 → 시간 상관 없음
            break;
          case WeatherConstants.MIN_TEMP:
            tmn = value;
            break;
          case WeatherConstants.RAIN_TYPE:
            if (timeDiff < minDiffPTY) {
              minDiffPTY = timeDiff;
              pty = value;
            }
            break;
          case WeatherConstants.SKY:
            if (timeDiff < minDiffSKY) {
              minDiffSKY = timeDiff;
              sky = value;
            }
            break;
          case WeatherConstants.WIND_SPEED:
            if (timeDiff < minDiffWSD) {
              minDiffWSD = timeDiff;
              wsd = value;
            }
            break;
          case WeatherConstants.RAIN_PROBABILITY:
            if (timeDiff < minDiffPOP) {
              minDiffPOP = timeDiff;
              pop = value;
            }
            break;
        }
      }

      log.info(
          "Parsed weather values -> TMP: {}, PTY: {}, SKY: {}, WSD: {}, POP: {}, TMX: {}, TMN: {}",
          tmp, pty, sky, wsd, pop, tmx, tmn);

      // 5. 날씨 상태 결정
      WeatherStatus status = WeatherStatus.fromWeatherData(pty, sky, Double.parseDouble(wsd));
      log.info("Weather Status: {}, Sky: {}, PTY: {}, WSD: {}", status, sky, pty, wsd);

      // 6. 가장 가까운 D-day 찾기 (날씨가 흐린 경우)
      //원래 코드
      Dday nearestDday = status == WeatherStatus.CLOUDY ? findNearestDday(user) : null;

//            // 맑음일 때 테스트
//            Dday nearestDday = status == WeatherStatus.SUNNY ? findNearestDday(user) : null;

      // 7. 응답 생성
      WeatherResponseDto weatherResponse = WeatherResponseDto.from(
          status,
          pet.getDogCat(),
          user.getDistrict().getName(), //3 단계만
          tmp,
          tmx,
          tmn,
          nearestDday,
          pop
      );

      log.info("Weather Response: {}", weatherResponse);
      return weatherResponse;

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

  /**
   * <pre>
   * 기상청 단기예보 API에서 사용할 수 있는 "가장 최근 발표 시각(base_time)"을 계산한다.
   *
   * - 발표 시각: 02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00 (하루 8번)
   * - 보통 발표 후 약 10분 뒤부터 조회 가능하다고 보고(안전하게) 10분 기준으로 보정한다.
   *   예) 02:10 이후 → base_time=0200 사용 가능
   *
   * 이 규칙을 지키지 않으면:
   * - 데이터 없음 / 에러 응답 / 502 Proxy Error 같은 문제가 발생할 수 있다.
   * </pre>
   */
  private static String resolveBaseTime(LocalDateTime now) {

    // 현재 시각을 HHmm 형태로 변환 (예: 01:35 → 135, 14:20 → 1420)
    int hhmm = now.getHour() * 100 + now.getMinute();

    // 최신 발표 시각부터 역순으로 체크
    if (hhmm >= 2310) {
      return "2300";
    }
    if (hhmm >= 2010) {
      return "2000";
    }
    if (hhmm >= 1710) {
      return "1700";
    }
    if (hhmm >= 1410) {
      return "1400";
    }
    if (hhmm >= 1110) {
      return "1100";
    }
    if (hhmm >= 810) {
      return "0800";
    }
    if (hhmm >= 510) {
      return "0500";
    }
    if (hhmm >= 210) {
      return "0200";
    }

    // 새벽 02:10 이전에는 오늘 데이터가 아직 없음
    // → 전날 23:00 발표 데이터를 사용해야 함
    return "2300";
  }

}