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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyBoardService {

    private final MyBoardRepository myBoardRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final int MAX_MYBOARD_COUNT = 6;
    private static final List<String> DEFAULT_BOARDS = List.of("홈", "꿀팁");

    /**
     * 마이게시판 등록 (등록 순서 유지, 등록한 게시판만 반환)
     */
    @Transactional
    public MyBoardResponseDto addMyBoard(Long boardId, String token) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 조회할 수 없습니다."));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        if (myBoardRepository.countByUser(user) >= MAX_MYBOARD_COUNT) {
            throw new IllegalArgumentException("최대 6개까지 등록이 가능합니다.");
        }

        if (myBoardRepository.existsByUserAndBoard(user, board)) {
            throw new IllegalArgumentException("이미 마이게시판에 등록된 게시판입니다.");
        }

        MyBoard newMyBoard = MyBoard.builder()
                .user(user)
                .board(board)
                .build();

        myBoardRepository.save(newMyBoard);

        return MyBoardResponseDto.builder()
                .id(newMyBoard.getId())
                .username(newMyBoard.getUser().getNickname())
                .boardName(newMyBoard.getBoard().getBoardName())
                .boardId(newMyBoard.getBoard().getId())
                .build();
    }

    /**
     * 마이게시판 목록 조회 (등록 순서 유지)
     */
    public List<MyBoardResponseDto> getMyBoards(String token) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 조회할 수 없습니다."));

        List<MyBoard> myBoards = myBoardRepository.findByUserOrderByIdAsc(user);

        return myBoards.stream()
                .map(board -> MyBoardResponseDto.builder()
                        .id(board.getId())
                        .username(board.getUser().getNickname())
                        .boardName(board.getBoard().getBoardName())
                        .boardId(board.getBoard().getId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 마이게시판 삭제
     */
    @Transactional
    public void removeMyBoard(Long myBoardId, String token) {
        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 조회할 수 없습니다."));

        MyBoard myBoard = myBoardRepository.findById(myBoardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 마이게시판이 존재하지 않습니다."));

        if (!myBoard.getUser().equals(user)) {
            throw new IllegalArgumentException("자신의 마이게시판만 삭제할 수 있습니다.");
        }

        myBoardRepository.delete(myBoard);
    }

    /**
     * 홈화면 조회 (기본 게시판 + 마이게시판 순서대로)
     */
    public List<String> getHomeBoards(String token) {
        findOrCreateDefaultBoards(); // 기본 게시판 자동 생성

        String userEmail = jwtTokenProvider.getUserEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 조회할 수 없습니다."));

        List<MyBoard> myBoards = myBoardRepository.findByUserOrderByIdAsc(user);

        // 기본 게시판을 DB에서 조회
        List<String> defaultBoards = boardRepository.findByBoardNameIn(DEFAULT_BOARDS)
                .stream().map(Board::getBoardName)
                .collect(Collectors.toList());

        List<String> homeBoards = new ArrayList<>(defaultBoards);
        homeBoards.addAll(myBoards.stream()
                .map(myBoard -> myBoard.getBoard().getBoardName())
                .collect(Collectors.toList()));

        return homeBoards;
    }

    /**
     * 기본 게시판이 존재하지 않으면 생성
     */
    @Transactional
    public void findOrCreateDefaultBoards() {
        List<Board> existingBoards = boardRepository.findByBoardNameIn(DEFAULT_BOARDS);

        Set<String> existingBoardNames = existingBoards.stream()
                .map(Board::getBoardName)
                .collect(Collectors.toSet());

        List<Board> boardsToSave = new ArrayList<>();

        for (String boardName : DEFAULT_BOARDS) {
            if (!existingBoardNames.contains(boardName)) {
                boardsToSave.add(Board.builder()
                        .boardName(boardName)
                        .content(boardName + " 게시판입니다.")
                        .build());
            }
        }

        if (!boardsToSave.isEmpty()) {
            boardRepository.saveAll(boardsToSave);
        }
    }
}
