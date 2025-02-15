package DC_square.spring.web.dto.response.community;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyBoardResponseDto {
    private Long id;
    private Long boardId;
    private String username;
    private String boardName;
}
