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

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyword> keywordList = new ArrayList<>();
    //orphanRemoval = true : board.getKeywordList().clear() 호출 시, 기존 키워드가 고아 객체로 간주되어 DB에서 삭제됩니다.

    @CreatedDate
    private LocalDateTime createdDate = LocalDateTime.now();


}
