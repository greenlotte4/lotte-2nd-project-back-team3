package BackAnt.dto.board;

import lombok.*;

import java.util.List;

/*
    날 짜 : 2024/12/10(월)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardPageDTO 생성
           - 글목록 페이징 처리
           -

*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPageDTO<T> {
    private List<T> content; // 실제 게시글 목록
    private int totalPages;  // 전체 페이지 수
    private Long totalElements; // 전체 게시글 수
    private int number; // 현재 페이지 번호

    public BoardPageDTO(org.springframework.data.domain.Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.number = page.getNumber();
    }

}
