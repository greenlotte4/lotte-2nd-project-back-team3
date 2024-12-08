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

*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseViewDTO {

    private Long id; // 게시글 번호
    private String cate1; // 카테고리1
    private String cate2; // 카테고리2
    private String title;    // 게시글 제목
    private int comment = 0; // 게시글 댓글 0
    private String content;  // 게시글 내용
    private String writer;   // 작성자
    private int file = 0; // 파일 0
    private int hit = 0; // 조회수 처음에 0
    private int likes = 0; // 좋아요 처음에 0
    private String regIp; // 작성일시
    private LocalDateTime regDate; // 작성일



    // ModelMapper 설정을 위한 정적 메서드
    public static ModelMapper createModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // writer 필드에 대한 매핑만 필요한 경우
        modelMapper.createTypeMap(Board.class, BoardResponseViewDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getWriter(), BoardResponseViewDTO::setWriter);
                });

        return modelMapper;
    }



}
