package DC_square.spring.domain.entity;

import DC_square.spring.domain.entity.place.Place;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "region")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(name = "Do", nullable = false, length = 20)
    private String Do;

    @Column(name = "si", nullable = false, length = 20)
    private String si;

    @Column(name = "gu", nullable = false, length = 20)
    private String gu;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places;
}
