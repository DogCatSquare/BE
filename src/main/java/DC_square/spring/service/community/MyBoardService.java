package DC_square.spring.service.community;

import DC_square.spring.domain.entity.community.Board;
import DC_square.spring.domain.entity.community.MyBoard;
import DC_square.spring.repository.community.BoardRepository;
import DC_square.spring.repository.community.MyBoardRepository;
import DC_square.spring.web.dto.response.community.MyBoardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyBoardService {

    private final MyBoardRepository myBoardRepository;
    private final BoardRepository boardRepository;

    private static final int MAX_MYBOARD_COUNT = 6;

    /**
     * 마이게시판 등록 (순서 지정 가능)
     */
    public MyBoardResponseDto addMyBoard(Long boardId, int orderIndex) {
        if(myBoardRepository.count() >= MAX_MYBOARD_COUNT) {
            throw new IllegalArgumentException("최대 6개까지 등록이 가능합니다.");
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다."));

        if (myBoardRepository.existsByBoard(board)) {
            throw new IllegalArgumentException("이미 마이게시판에 등록된 게시판입니다.");
        }

        if (orderIndex < 1 || orderIndex > MAX_MYBOARD_COUNT) {
            throw new IllegalArgumentException("순서는 1부터 6까지만 설정할 수 있습니다.");
        }

        List<MyBoard> existingBoards = myBoardRepository.findAllByOrderByOrderIndexAsc();

        //순서가 이미 존재하면 밀어내기
        for(MyBoard myBoard : existingBoards) {
            if (myBoard.getOrderIndex() >= orderIndex) {
                myBoard.setOrderIndex(myBoard.getOrderIndex() + 1);
            }
        }

        MyBoard newMyBoard = MyBoard.builder()
                .board(board)
                .orderIndex(orderIndex)
                .build();

        MyBoard savedMyBoard = myBoardRepository.save(newMyBoard);
        myBoardRepository.saveAll(existingBoards); //변경된 마이게시판의 목록을 한 번에 저장(업데이트)

        return MyBoardResponseDto.builder()
                .id(savedMyBoard.getId())
                .boardId(savedMyBoard.getBoard().getId())
                .boardName(savedMyBoard.getBoard().getBoardName())
                .orderIndex(savedMyBoard.getOrderIndex())
                .build();
    }

    /**
     * 마이게시판 순서 변경
     */
    public MyBoardResponseDto updateOrder(Long myBoardId, int newOrderIndex) {
        if (newOrderIndex < 1 || newOrderIndex > MAX_MYBOARD_COUNT) {
            throw new IllegalArgumentException("순서는 1부터 6까지만 설정할 수 있습니다.");
        }

        MyBoard targetBoard = myBoardRepository.findById(myBoardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 마이게시판이 존재하지 않습니다."));

        List<MyBoard> existingBoards = myBoardRepository.findAllByOrderByOrderIndexAsc();
        existingBoards.remove(targetBoard);  // 현재 순서를 변경할 마이게시판을 리스트에서 제거

        // 순서가 변경될 게시판들의 순서 밀기
        for (MyBoard myBoard : existingBoards) {
            if (myBoard.getOrderIndex() >= newOrderIndex) {
                myBoard.setOrderIndex(myBoard.getOrderIndex() + 1);  // 순서가 변경된 게시판보다 뒤쪽의 순서를 밀어냄
            }
        }

        // 타겟 게시판의 순서 변경
        targetBoard.setOrderIndex(newOrderIndex);
        existingBoards.add(targetBoard);  // 변경된 타겟 게시판 추가
        existingBoards.sort(Comparator.comparingInt(MyBoard::getOrderIndex));  // 순서대로 정렬

        myBoardRepository.saveAll(existingBoards);  // 변경된 순서대로 모두 저장

        return MyBoardResponseDto.builder()
                .id(targetBoard.getId())
                .boardId(targetBoard.getBoard().getId())
                .boardName(targetBoard.getBoard().getBoardName())
                .orderIndex(targetBoard.getOrderIndex())
                .build();
    }
    /**
     * 마이게시판 목록 조회(순서대로 정렬)
     */
    public List<MyBoardResponseDto> getMyBoards() {
        List<MyBoard> myBoards = myBoardRepository.findAllByOrderByOrderIndexAsc();

        return myBoards.stream()
                .map(board -> new MyBoardResponseDto(
                        board.getId(),
                        board.getBoard().getId(),
                        board.getBoard().getBoardName(),
                        board.getOrderIndex()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 마이게시판 삭제
     */
    public void removeMyBoard(Long myBoardId) {
        MyBoard myBoard = myBoardRepository.findById(myBoardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 마이게시판이 존재하지 않습니다."));

        myBoardRepository.delete(myBoard);

        //삭제 후 남아있는 마이게시판 목록을 가져오기 (순서 오름차순 정렬)
        List<MyBoard> remainingBoards = myBoardRepository.findAllByOrderByOrderIndexAsc();

        //순서를 1부터 다시 설정(삭제된 게시판의 순서 이후 것들을 당기기)
        for(int i = 0; i < remainingBoards.size(); i++) {
            remainingBoards.get(i).setOrderIndex(i + 1); //i번째 요소의 orderIndex 값을 i+1로 설정
            // => orderIndex를 1부터 시작하도록 설정하는 방법(리스트의 인덱스는 0부터 시작하지만 순서는 1부터 시작)
        }

        myBoardRepository.saveAll(remainingBoards);
    }
}
