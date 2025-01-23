package DC_square.spring.service.community;

import DC_square.spring.config.S3.AmazonS3Manager;
import DC_square.spring.config.S3.Uuid;
import DC_square.spring.config.S3.UuidRepository;
import DC_square.spring.domain.entity.community.Board;
import DC_square.spring.domain.entity.community.Keyword;
import DC_square.spring.repository.community.BoardRepository;
import DC_square.spring.web.dto.request.community.BoardRequestDto;
import DC_square.spring.web.dto.response.community.BoardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    /**
     * 게시판 생성 API
     */
    public BoardResponseDto createdBoard(BoardRequestDto boardRequestDto, MultipartFile boardPicture) {

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        String pictureUrl = s3Manager.uploadFile(s3Manager.generateCommunity(savedUuid),boardPicture);


        //Board 엔티티 생성
        Board board = Board.builder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .imagePath(pictureUrl)
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
                .title(savedBoard.getTitle())
                .imagePath(savedBoard.getImagePath())
                .content(savedBoard.getContent())
                .keywords(keywordList.stream()
                        .map(Keyword::getKeyword)
                        .toList())
                .createDate(LocalDateTime.now())
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
                .title(board.getTitle())
                .content(board.getContent())
                .imagePath(board.getImagePath())
                .keywords(keywords)
                .createDate(LocalDateTime.now())
                .build();
    }








}
