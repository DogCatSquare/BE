package DC_square.spring.service.community;

import DC_square.spring.domain.entity.community.Board;
import DC_square.spring.domain.entity.community.Keyword;
import DC_square.spring.repository.community.BoardRepository;
import DC_square.spring.web.dto.request.community.BoardRequestDto;
import DC_square.spring.web.dto.response.community.BoardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 게시판 생성 API
     */
    public BoardResponseDto createdBoard(BoardRequestDto boardRequestDto) {
        //Board 엔티티 생성
        Board board = Board.builder()
                .boardName(boardRequestDto.getBoardName())
                .content(boardRequestDto.getContent())
                .build();

        // 키워드 매핑(List<String> keywords -> List<Keyword> keywordList)
        List<Keyword> keywordList = boardRequestDto.getKeywords().stream()
                .map(keyword -> Keyword.builder()
                        .keyword(keyword)
                        .board(board) // Board와 연관 설정
                        .build())
                .toList();

        //키워드를 Board에 설정
        board.setKeywordList(keywordList);


        //저장
        Board savedBoard = boardRepository.save(board);

        //BoardResponseDto 생성해서 반환
        return BoardResponseDto.builder()
                .id(savedBoard.getId())
                .boardName(savedBoard.getBoardName())
                .content(savedBoard.getContent())
                .keywords(keywordList.stream()
                        .map(Keyword::getKeyword)
                        .toList())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 게시판 조회 API
     */
    public BoardResponseDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판 id가 존재하지 않습니다."));

        List<String> keywords = board.getKeywordList().stream()
                .map(Keyword::getKeyword)
                .toList();

        return BoardResponseDto.builder()
                .id(board.getId())
                .boardName(board.getBoardName())
                .content(board.getContent())
                .keywords(keywords)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto boardRequestDto) {
        // 1. Board를 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판 id가 존재하지 않습니다."));

        // 2. Board 이름과 내용 업데이트
        board.setBoardName(boardRequestDto.getBoardName());
        board.setContent(boardRequestDto.getContent());

        // 3. 키워드가 요청에 포함된 경우에만 업데이트
        if (boardRequestDto.getKeywords() != null && !boardRequestDto.getKeywords().isEmpty()) {
            // 기존 키워드 리스트를 비우고 새 키워드 추가
            board.getKeywordList().clear(); //리스트 비우기(orphanRemoval = true로 인해 db에서도 지워짐)
            List<Keyword> keywordList = boardRequestDto.getKeywords().stream()
                    .map(keyword -> Keyword.builder()
                            .keyword(keyword)
                            .board(board) // Board와 연관 설정
                            .build())
                    .toList();
            board.getKeywordList().addAll(keywordList);
        }

        // 4. 변경된 Board 저장
        Board updatedBoard = boardRepository.save(board);

        // 5. BoardResponseDto 생성 및 반환
        return BoardResponseDto.builder()
                .id(updatedBoard.getId())
                .boardName(updatedBoard.getBoardName())
                .content(updatedBoard.getContent())
                .keywords(updatedBoard.getKeywordList().stream()
                        .map(Keyword::getKeyword)
                        .toList())
                .createdAt(LocalDateTime.now())
                .build();
    }


    /**
     * 게시판 삭제 API
     */
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }








}
