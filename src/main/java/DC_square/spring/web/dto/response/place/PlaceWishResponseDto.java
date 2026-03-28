package DC_square.spring.web.dto.response.place;

import DC_square.spring.domain.enums.PlaceCategory;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceWishResponseDto {

  private String googlePlaceId;
  private String name;
  private String address;
  private PlaceCategory category;
  private String phoneNumber;
  private Double longitude;
  private Double latitude;
  private Double distance;
  private Boolean open;
  private String imgUrl;
  private Integer reviewCount;
  private List<String> keywords;
  private List<WalkDto> walks;

  @Getter
  @Builder
  public static class WalkDto {
    private Long walkId;
    private String title;
    private Double distance;
    private Integer time;
    private List<String> walkImageUrl;
  }
}
