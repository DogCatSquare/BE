package DC_square.spring.domain.entity.place;

import DC_square.spring.domain.entity.Region;
import DC_square.spring.domain.enums.PlaceCategory;
import  jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Enumerated(EnumType.STRING) // enum -> 문자열로 저장
    @Column(name = "category", nullable = false)
    private PlaceCategory category;

    @Column(name = "phone_number", nullable = false, length = 12)
    private String phoneNumber;

    @Column(name = "open") // NULL 허용
    private Boolean open;

    @Column(name = "view", nullable = false)
    private Integer view;

    // 조회수 초기값 0으로 설정
    @PrePersist
    public void prePersist() {
        this.view = (this.view == null) ? 0 : this.view;
    }

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "longitude", nullable = false) // -180.000000 ~ 180.000000
    private Double longitude;

    @Column(name = "latitude", nullable = false) // -180.000000 ~ 180.000000
    private Double latitude;

    @OneToOne(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlaceDetail placeDetail;
}

