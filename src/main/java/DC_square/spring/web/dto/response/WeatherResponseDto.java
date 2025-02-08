package DC_square.spring.web.dto.response;

import DC_square.spring.constant.WeatherConstants;
import DC_square.spring.domain.entity.Dday;
import DC_square.spring.domain.enums.DogCat;
import DC_square.spring.domain.enums.WeatherStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class WeatherResponseDto {
    private String mainMessage;     // 메인 메시지
    private String subMessage;      // 보조 메시지
    private String location;        // 지역 정보 (ex: 강남구 역삼동)
    private String currentTemp;     // 현재 기온
    private String maxTemp;         // 최고 기온
    private String minTemp;         // 최저 기온
    private String imageUrl;        // S3 이미지 URL

    // D-day 관련 정보 (날씨가 흐릴 때만 사용)
    private String ddayTitle;       // D-day 제목
    private String ddayMessage;     // D-day 메시지 (D-3)
    private String ddayDate;        // 날짜 (2024년 2월 10일)

    public static WeatherResponseDto from(
            WeatherStatus status,
            DogCat petType,
            String location,
            String currentTemp,
            String maxTemp,
            String minTemp,
            Dday dday
    ) {
        WeatherResponseDtoBuilder builder = WeatherResponseDto.builder()
                .location(location)
                .currentTemp(currentTemp + "°")
                .maxTemp(maxTemp + "°")
                .minTemp(minTemp + "°");

        // D-day가 있고 날씨가 흐린 경우
        if (dday != null && status == WeatherStatus.CLOUDY) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
            builder.ddayTitle(dday.getTitle())
                    .ddayMessage("D-" + dday.getDaysLeft())
                    .ddayDate(dday.getDay().format(formatter));

            setDdayMessage(builder, petType, dday);
            return builder.build();
        }

        // 날씨에 따른 메시지
        setWeatherMessage(builder, status, petType);
        return builder.build();
    }

    private static void setWeatherMessage(WeatherResponseDtoBuilder builder, WeatherStatus status, DogCat petType) {
        boolean isDog = petType == DogCat.DOG;

        switch (status) {
            case SUNNY:
                builder.mainMessage("오늘 날씨 맑음")
                        .subMessage(isDog ? "산책하기 좋은 날" : "환기하기 좋은 날")
                        .imageUrl(isDog ? WeatherConstants.DOG_SUN_IMAGE : WeatherConstants.CAT_SUN_IMAGE);
                break;
            case RAINY:
                builder.mainMessage("비 오는 날")
                        .subMessage(isDog ? "산책에 유의하세요" : "창문을 닫아주세요")
                        .imageUrl(isDog ? WeatherConstants.DOG_RAIN_IMAGE : WeatherConstants.CAT_RAIN_IMAGE);
                break;
            case SNOWY:
                builder.mainMessage("눈 오는 날")
                        .subMessage(isDog ? "산책에 유의하세요" : "전기장판을 꺼내요")
                        .imageUrl(isDog ? WeatherConstants.DOG_SNOW_IMAGE : WeatherConstants.CAT_SNOW_IMAGE);
                break;
            case WINDY:
                builder.mainMessage("바람 부는날")
                        .subMessage(isDog ? "산책에 유의하세요" : "털날림 주의")
                        .imageUrl(isDog ? WeatherConstants.DOG_WIND_IMAGE : WeatherConstants.CAT_WIND_IMAGE);
                break;
        }
    }

    private static void setDdayMessage(WeatherResponseDtoBuilder builder, DogCat petType, Dday dday) {
        boolean isDog = petType == DogCat.DOG;

        switch (dday.getType()) {
            case FOOD:
                builder.mainMessage("사료 주문일이")
                        .subMessage("다가오고있어요")
                        .imageUrl(isDog ? WeatherConstants.DOG_BOX_IMAGE : WeatherConstants.CAT_BOX_IMAGE);
                break;
            case PAD:
                builder.mainMessage(isDog ? "패드 구매일이" : "모래 구매일이")
                        .subMessage("다가오고있어요")
                        .imageUrl(isDog ? WeatherConstants.DOG_BOX_IMAGE : WeatherConstants.CAT_BOX_IMAGE);
                break;
            case HOSPITAL:
                builder.mainMessage("병원 방문일이")
                        .subMessage("다가오고있어요")
                        .imageUrl(isDog ? WeatherConstants.DOG_HOSPITAL_IMAGE : WeatherConstants.CAT_HOSPITAL_IMAGE);
                break;
        }
    }
}