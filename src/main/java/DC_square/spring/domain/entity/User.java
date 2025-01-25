package DC_square.spring.domain.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 10)
    private String nickname;  // username을 nickname으로 변경

    @Column(nullable = false, length = 11)
    private String phoneNumber;

    @Column
    private String regionId;

    //최근 사료 구입 날짜
    @Column
    private String foodDate;
    //사료 구매 주기
    @Column
    private Integer foodDuring;
    // 병원 방문 날짜
    @Column
    private String hospitalDate;

    public Long getId() {
        return id;
    }

    //광고 동의 여부
    @Column
    private Boolean adAgree;
}