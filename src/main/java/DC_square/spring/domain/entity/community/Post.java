package DC_square.spring.domain.entity.community;

import DC_square.spring.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "video_URL")
    private String video_URL;

    @ColumnDefault("0")
    @Column(name = "like_count",nullable = false)
    private Integer likeCount;

    @ColumnDefault("0")
    @Column(name = "comment_count",nullable = false)
    private Integer commentCount;

    @Column(name = "img_URL")
    private String img_URL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id",nullable = false)
    private Board board;

    @CreatedDate
    private LocalDateTime created_at = LocalDateTime.now();

}