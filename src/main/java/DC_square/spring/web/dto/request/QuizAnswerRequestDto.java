package DC_square.spring.web.dto.request;

import DC_square.spring.domain.enums.QuizAnswer;
import lombok.Getter;

@Getter
public class QuizAnswerRequestDto {

  private QuizAnswer selectedAnswer; // "O" 또는 "X"
}
