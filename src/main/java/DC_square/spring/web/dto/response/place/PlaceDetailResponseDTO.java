package DC_square.spring.web.dto.response.place;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder

public class PlaceDetailResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String category;
    private String phoneNumber;
    private Boolean open;
    private Double longitude;
    private Double latitude;
    private String businessHours;
    private String homepageUrl;
    private String description;
    private List<String> facilities;
}
