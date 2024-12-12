package BackAnt.dto.board;

import BackAnt.entity.User;
import BackAnt.entity.board.Board;
import lombok.*;

import java.time.LocalDateTime;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardDTO 생성

    수정 내역 :
    2024/12/12(목) - 김민희 : 1. Entity를 DTO로 변환하는 정적 메서드 생성
                            2. · writer 필드명 -> writerId (작성자 ID) 수정
                               · writerName (작성자 이름) 필드 추가


*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {

    private Long id; // 게시글 번호

    private String cate1; // 카테고리1
    private String cate2; // 카테고리2

    private String title;    // 게시글 제목
    private String content;  // 게시글 내용

    private Long writerId;   // 작성자 ID
    private String writerName; // 작성자 이름

    @Builder.Default
    private int file = 0; // 파일 0

    @Builder.Default
    private int hit = 0; // 조회수 처음에 0

    @Builder.Default
    private int likes = 0; // 좋아요 처음에 0

    @Builder.Default
    private int comment = 0; // 게시글 댓글 0

    private String regIp; // 작성일시
    private LocalDateTime regDate; // 작성일


    // Entity -> DTO 변환
    public static BoardDTO of(Board board) {
        User writer = board.getWriter();  // User 객체 조회
        return BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writerId(writer != null ? writer.getId() : null)
                .writerName(writer != null ? writer.getName() : "익명") // 작성자 이름 가져오기
                .file(board.getFile())
                .hit(board.getHit())
                .likes(board.getLikes())
                .comment(board.getComment())
                .regIp(board.getRegIp())
                .regDate(board.getRegDate())
                .build();
    }



}
