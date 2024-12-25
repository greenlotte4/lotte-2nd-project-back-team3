package BackAnt.dto.board;

import BackAnt.entity.User;
import BackAnt.entity.board.Board;
import BackAnt.service.board.BoardCategoryService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardDTO 생성

    수정 내역 :
    2024/12/12(목) - 김민희 : 1. Entity를 DTO로 변환하는 정적 메서드 생성
                            2. · writer 필드명 -> writerId (작성자 ID) 수정
                               · writerName (작성자 이름) 필드 추가
    2024/12/24(화) - 김민희 : 게시판 검색을 위한 키워드 필드 추가

*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j2
public class BoardDTO {


    private Long id; // 게시글 번호

    private String title;    // 게시글 제목
    private String content;  // 게시글 내용

    private Long writerId;   // 작성자 ID
    private String writerName; // 작성자 이름

    // 게시글 카테고리
    private Long categoryId;

    @Builder.Default
    private int file = 0; // 파일 0

    @Builder.Default
    private int hit = 0; // 조회수 처음에 0

    @Builder.Default
    private int likes = 0; // 좋아요 처음에 0

    @Builder.Default
    private int comment = 0; // 게시글 댓글 0

    private String regIp; // 작성 일시

    // 검색에 사용되는 키워드 추가
    private String keyword; // 검색 키워드 (제목, 내용, 글쓴이에서 검색)

    // 날짜 필드에 포맷 지정
    // @JsonFormat을 사용하여 LocalDateTime을 지정된 형식으로 포맷
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime regDate; // 작성일


    // Entity -> DTO 변환
    public static BoardDTO of(Board board, int likeCount) {
        User writer = board.getWriter();  // User 객체 조회


        BoardDTO boardDTO = BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .categoryId(board.getCategory().getId())
                .writerId(writer != null ? writer.getId() : null)
                .writerName(writer != null ? writer.getName() : "익명") // 작성자 이름 가져오기
                .file(board.getFile())
                .hit(board.getHit())
                .likes(board.getLikes())
                .likes(likeCount) // 좋아요 수를 전달
                .comment(board.getComment())
                .regIp(board.getRegIp())
                .regDate(board.getRegDate())
                .build();
        log.info("(DTO 변환) Board -> BoardDTO: " + boardDTO);
        return boardDTO;
    }




}
