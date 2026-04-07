package DC_square.spring.web.dto.response.quiz;

import DC_square.spring.domain.enums.DogCat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizResponseDto {

  private Long quizId;
  private DogCat category;
  private String question;
}
