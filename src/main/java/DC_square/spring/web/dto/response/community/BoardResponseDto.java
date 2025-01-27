package DC_square.spring.web.dto.response.community;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {

    private Long id;

    private String boardName;

    private String content;

    private List<String> keywords;

    private LocalDateTime createDate;
}
