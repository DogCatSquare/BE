package DC_square.spring.web.dto.response.quiz;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizResultResponseDto {

  private boolean isCorrect; // 정답 여부
  private String correctAnswer; // 실제 정답 ("O" or "X)
  private String explanation; // 해설
}
