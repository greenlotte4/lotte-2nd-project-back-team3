package BackAnt.dto.board;
import BackAnt.entity.board.Board;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

/*
    날 짜 : 2024/12/06(금)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardResponseViewDTO 생성
           - 글목록 상세 조회
           - 댓글 기능 추가 시 DTO 추가 예정

    수정 내역: 2024/12/23 (김민희) :  카테고리 삭제

*/
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardResponseViewDTO {

    private Long id; // 게시글 번호

    private String title;    // 게시글 제목
    private int comment = 0; // 게시글 댓글 0
    private String content;  // 게시글 내용
    private String writer;   // 작성자 이름 (String 타입으로 작성자 이름을 받음)
    private String writerName;   // 작성자 이름 (String 타입으로 작성자 이름을 받음)

    private int file = 0; // 파일 0
    private int hit = 0; // 조회수 처음에 0
    private int likes = 0; // 좋아요 처음에 0

    private String regIp; // 작성일시
    private LocalDateTime regDate; // 작성일


    public BoardResponseViewDTO(Long id, String title, int comment, String content, String writer,
                                int file, int hit, int likes, String regIp, LocalDateTime regDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.comment = comment;
        this.file = file;
        this.hit = hit;
        this.likes = likes;
        this.regIp = regIp;
        this.regDate = regDate;
    }


//    // ModelMapper 설정을 위한 정적 메서드
//    public static ModelMapper createModelMapper() {
//        ModelMapper modelMapper = new ModelMapper();
//
//        // writer 필드에 대한 매핑만 필요한 경우
//        modelMapper.createTypeMap(Board.class, BoardResponseViewDTO.class)
//                .addMappings(mapper -> {
//                    mapper.map(src -> src.getWriter(), BoardResponseViewDTO::setWriter);
//                });
//
//        return modelMapper;
//    }
    
    // ModelMapper 설정을 위한 정적 메서드
    public static ModelMapper createModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Board의 writer는 User 객체일 경우, User의 name을 writer로 매핑
        modelMapper.createTypeMap(Board.class, BoardResponseViewDTO.class)
                .addMappings(mapper -> {
                    // writer 필드는 Board의 writer(User)의 name을 사용하도록 매핑
                    mapper.map(src -> src.getWriter() != null ? src.getWriter().getName() : "익명",
                            BoardResponseViewDTO::setWriter);
                });

        return modelMapper;
}


}
