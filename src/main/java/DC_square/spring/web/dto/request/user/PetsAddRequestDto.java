package DC_square.spring.web.dto.request.user;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Getter
@Setter
public class PetsAddRequestDto {
    @NotEmpty(message = "최소 한 마리의 반려동물 정보가 필요합니다.")
    @Valid
    private PetRegistrationDto pet;
}