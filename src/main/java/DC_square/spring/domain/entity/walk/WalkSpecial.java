package DC_square.spring.domain.entity.walk;

import DC_square.spring.domain.enums.Special;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkSpecial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "special_type", nullable = false)
    private Special specialType;

    @Column(name = "custom_value")
    private String customValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_id")
    private Walk walk;

    public WalkSpecial(Special specialType, String customValue, Walk walk) {
        this.specialType = specialType;
        this.customValue = customValue;
        this.walk = walk;
    }
}
