package DC_square.spring.domain.entity.walk;

import DC_square.spring.domain.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WalkWish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "walk_id")
    private Walk walk;

    private boolean isWished;

    public WalkWish(User user, Walk walk, boolean isWished) {
        this.user = user;
        this.walk = walk;
        this.isWished = isWished;
    }
}