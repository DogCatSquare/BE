package DC_square.spring.domain.entity;

import DC_square.spring.domain.enums.DdayType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false)
    private LocalDate day;

    @Column
    private Integer term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private DdayType type;

    @Column
    private String imageUrl;

    public void setDefaultImageUrl() {
        switch(this.type) {
            case FOOD: this.imageUrl = "https://dogcatsquare.s3.ap-northeast-2.amazonaws.com/dday/food-icon.png";
                break;
            case PAD: this.imageUrl = "https://dogcatsquare.s3.ap-northeast-2.amazonaws.com/dday/pad-icon.png";
                break;
            case HOSPITAL: this.imageUrl = "https://dogcatsquare.s3.ap-northeast-2.amazonaws.com/dday/hospital-icon.png";
                break;
            case CUSTOM: this.imageUrl = "https://dogcatsquare.s3.ap-northeast-2.amazonaws.com/dday/custom-icon.png";
                break;
        }
    }
}