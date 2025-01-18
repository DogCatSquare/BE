package DC_square.spring.domain.entity.place;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cost")
@Getter
@Setter
public class Cost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @ManyToOne
    @JoinColumn(name = "placeDetail_id", nullable = false)
    private PlaceDetail placeDetail;
}
