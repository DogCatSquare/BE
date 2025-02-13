package DC_square.spring.web.dto.request.place;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceUserInfoUpdateDTO {
    private List<String> keywords;
    private String additionalInfo;
}
