package DC_square.spring.domain.entity.region;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 시/구 이름 (예: 강남구, 수원시)

    @Column(nullable = false)
    private Integer gridX;  // 기상청 X좌표

    @Column(nullable = false)
    private Integer gridY;  // 기상청 Y좌표


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<District> districts;
}