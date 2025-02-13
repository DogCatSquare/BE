package DC_square.spring.domain.entity.place;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "placeDetail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "businessHours", length = 225)
    private String businessHours;

    @Column(name = "homepage_url")
    private String homepageUrl;

    @ElementCollection
    @Column(name = "facilities")
    private List<String> facilities = new ArrayList<>();

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "place_id", nullable = false) // Place의 외래 키
    private Place place;

    @OneToMany(mappedBy = "placeDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cost> costs;

    @Column(name = "additional_info", length = 500)
    private String additionalInfo;
}
