package DC_square.spring.domain.entity.place;

import DC_square.spring.domain.entity.Region;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "place_image")
@Getter
@Setter
public class PlaceImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}
