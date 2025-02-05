package DC_square.spring.domain.entity.walk;

import DC_square.spring.domain.entity.Coordinate;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.Difficulty;
import DC_square.spring.domain.enums.Special;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private Double distance;

    private Integer time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "walk", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WalkSpecial> specials = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "walk_images",
            joinColumns = @JoinColumn(name = "walk_id")
    )
    @Column(name = "image_url")
    private List<String> walkImageUrl;

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

    public void updateReviewCount(int count) {
        this.reviewCount = count;
    }
}