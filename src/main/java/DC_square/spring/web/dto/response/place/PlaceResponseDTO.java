package DC_square.spring.web.dto.response.place;

import DC_square.spring.domain.entity.place.PlaceImage;
import DC_square.spring.domain.enums.PlaceCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class PlaceResponseDTO {
    private Long id;
    private String name;
    private String address;
    private PlaceCategory category;
    private String phoneNumber;
    private Double distance;
    private Boolean open;
    private Long regionId;
    private String imgUrl;
}
