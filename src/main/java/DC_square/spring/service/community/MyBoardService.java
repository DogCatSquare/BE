package DC_square.spring.service.community;

import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.entity.community.Board;
import DC_square.spring.domain.entity.community.MyBoard;
import DC_square.spring.repository.community.BoardRepository;
import DC_square.spring.repository.community.MyBoardRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.response.community.MyBoardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyBoardService {

    private final MyBoardRepository myBoardRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final int MAX_MYBOARD_COUNT = 6;

    /**
     * 마이게시판 등록
     */
    public MyBoardResponseDto addMyBoard(Long boardId, String token) {

        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 조회할 수 없습니다."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        if(myBoardRepository.countByUser(user) >= MAX_MYBOARD_COUNT) {
            throw new IllegalArgumentException("최대 6개까지 등록이 가능합니다.");
        }

        // 특정 사용자가 해당 게시판을 마이게시판에 등록했는지 확인
        if (myBoardRepository.existsByUserAndBoard(user, board)) {
            throw new IllegalArgumentException("이미 마이게시판에 등록된 게시판입니다.");
        }

        MyBoard newMyBoard = MyBoard.builder()
                .user(user)
                .board(board)
                .build();

        MyBoard savedMyBoard = myBoardRepository.save(newMyBoard);

        return MyBoardResponseDto.builder()
                .id(savedMyBoard.getId())
                .username(savedMyBoard.getUser().getNickname())
                .boardId(savedMyBoard.getBoard().getId())
                .boardName(savedMyBoard.getBoard().getBoardName())
                .build();
    }


    /**
     * 마이게시판 목록 조회
     */
    public List<MyBoardResponseDto> getMyBoards(String token) {

        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 조회할 수 없습니다."));
        // 특정 사용자의 마이게시판만 조회
        List<MyBoard> myBoards = myBoardRepository.findByUser(user);

        return myBoards.stream()
                .map(board -> MyBoardResponseDto.builder()
                        .id(board.getId())
                        .username(board.getUser().getNickname())
                        .boardName(board.getBoard().getBoardName())
                        .boardId(board.getBoard().getId())
                        .build()
                ).collect(Collectors.toList());
    }


    /**
     * 마이게시판 삭제
     */
    public void removeMyBoard(Long myBoardId, String token) {

        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 조회할 수 없습니다."));

        MyBoard myBoard = myBoardRepository.findById(myBoardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 마이게시판이 존재하지 않습니다."));

        myBoardRepository.delete(myBoard);
    }
}
