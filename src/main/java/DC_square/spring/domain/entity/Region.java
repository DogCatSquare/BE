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

    @Column(name = "do_name", nullable = false, length = 20) // 'Do'를 'doName'으로 변경 -> sql예약어랑 문제
    private String doName;

    @Column(name = "si", nullable = false, length = 20)
    private String si;

    @Column(name = "gu", nullable = false, length = 20)
    private String gu;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Place> places;

    @Column(name = "latitude") // -180.000000 ~ 180.000000
    private Double latitude; // 예시

}
