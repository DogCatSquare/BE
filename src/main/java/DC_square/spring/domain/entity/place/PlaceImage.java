package DC_square.spring.domain.entity.place;

import DC_square.spring.domain.entity.Region;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_image")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "photo_reference", nullable = false)
    private String photoReference;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}
