package DC_square.spring.domain.entity.community;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "title",nullable = false)
    private String boardName;

    @Column(name = "content",nullable = false)
    private String content;

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL)
    private List<Keyword> keywordList = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdDate = LocalDateTime.now();


}
