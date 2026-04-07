package DC_square.spring.web.controller;

import DC_square.spring.apiPayload.ApiResponse;
import DC_square.spring.service.QuizService;
import DC_square.spring.web.dto.request.QuizAnswerRequestDto;
import DC_square.spring.web.dto.response.quiz.QuizResponseDto;
import DC_square.spring.web.dto.response.quiz.QuizResultResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
@Tag(name = "Quiz API", description = "반려동물 OX 퀴즈 API")
public class QuizController {

  private final QuizService quizService;

  // 홈 화면에서 랜덤 퀴즈 1개 조회
  @Operation(summary = "랜덤 퀴즈 조회", description = "홈 화면에 표시할 랜덤 OX 퀴즈 1개를 반환합니다.")
  @GetMapping("/random")
  public ApiResponse<QuizResponseDto> getRandomQuiz() {
    return ApiResponse.onSuccess(quizService.getRandomQuiz());
  }

  // 특정 퀴즈 조회
  @Operation(summary = "퀴즈 단건 조회", description = "quizId로 특정 퀴즈를 조회합니다.")
  @GetMapping("/{quizId}")
  public ApiResponse<QuizResponseDto> getQuiz(@PathVariable Long quizId) {
    return ApiResponse.onSuccess(quizService.getQuiz(quizId));
  }

  // 정답 제출
  @Operation(summary = "정답 제출", description = "O 또는 X를 선택해 정답 여부와 해설을 받습니다.")
  @PostMapping("/{quizId}/answer")
  public ApiResponse<QuizResultResponseDto> checkAnswer(
      @PathVariable Long quizId,
      @RequestBody QuizAnswerRequestDto requestDto
  ) {
    return ApiResponse.onSuccess(quizService.checkAnswer(quizId, requestDto));
  }
}
