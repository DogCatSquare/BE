package DC_square.spring.domain.entity;

import DC_square.spring.domain.enums.DogCat;
import DC_square.spring.domain.enums.QuizAnswer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String question;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DogCat category;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private QuizAnswer answer;

  @Column(nullable = false)
  private String explanation; // 정답 해설
}
