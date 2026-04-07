package DC_square.spring.service;

import DC_square.spring.domain.entity.Quiz;
import DC_square.spring.repository.QuizRepository;
import DC_square.spring.web.dto.request.QuizAnswerRequestDto;
import DC_square.spring.web.dto.response.quiz.QuizResponseDto;
import DC_square.spring.web.dto.response.quiz.QuizResultResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {

  private final QuizRepository quizRepository;

  // 홈 화면용 랜덤 퀴즈 1개 조회
  public QuizResponseDto getRandomQuiz() {
    Quiz quiz = quizRepository.findRandom()
        .orElseThrow(() -> new RuntimeException("퀴즈가 존재하지 않습니다."));

    return QuizResponseDto.builder()
        .quizId(quiz.getId())
        .category(quiz.getCategory())
        .question(quiz.getQuestion())
        .build();
  }

  // 특정 퀴즈 조회
  public QuizResponseDto getQuiz(Long quizId) {
    Quiz quiz = quizRepository.findById(quizId)
        .orElseThrow(() -> new RuntimeException("해당 퀴즈가 존재하지 않습니다."));

    return QuizResponseDto.builder()
        .quizId(quiz.getId())
        .category(quiz.getCategory())
        .question(quiz.getQuestion())
        .build();
  }

  // 정답 제출 및 결과 반환
  public QuizResultResponseDto checkAnswer(Long quizId, QuizAnswerRequestDto requestDto) {
    Quiz quiz = quizRepository.findById(quizId)
        .orElseThrow(() -> new RuntimeException("해당 퀴즈가 존재하지 않습니다."));

    boolean isCorrect = quiz.getAnswer() == requestDto.getSelectedAnswer();

    return QuizResultResponseDto.builder()
        .isCorrect(isCorrect)
        .correctAnswer(quiz.getAnswer().name())
        .explanation(quiz.getExplanation())
        .build();
  }
}
