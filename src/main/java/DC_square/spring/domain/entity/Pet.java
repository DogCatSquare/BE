package DC_square.spring.domain.entity;

import DC_square.spring.domain.enums.DogCat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String petName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DogCat dogCat;

    @Column(nullable = false, length = 20)
    private String breed;

    @Column(nullable = false)
    private LocalDate birth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}