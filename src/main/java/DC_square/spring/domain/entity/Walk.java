package DC_square.spring.domain.entity;

import DC_square.spring.domain.enums.Difficulty;
import DC_square.spring.domain.enums.Special;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Walk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    private Integer reviewCount;

    @Column(nullable = false)
    private Double distance;

    private Integer time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @ElementCollection
    @CollectionTable(name = "walk_specials",
            joinColumns = @JoinColumn(name = "walk_id"))
    @Column(name = "special")
    @Enumerated(EnumType.STRING)
    private List<Special> specials = new ArrayList<>();

    //private String customSpecial;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "walk_coordinates",
            joinColumns = @JoinColumn(name = "walk_id"))
    private List<Coordinate> coordinates = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdBy;
}