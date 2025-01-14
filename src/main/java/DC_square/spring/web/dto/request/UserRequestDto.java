package DC_square.spring.web.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRequestDto {

    private String username;
    private String email;
    private String password;
}
