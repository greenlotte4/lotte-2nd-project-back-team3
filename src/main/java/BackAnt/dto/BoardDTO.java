package BackAnt.dto;

import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/*
    날 짜 : 2024/12/02(월)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardDTO 생성
*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {

    private Long id;

    @Column(nullable = false, length = 100)   // NULL 불가, 최대 100자
    private String title;    // 게시글 제목

    @Builder.Default
    private int comment = 0; // 게시글 댓글 0

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;  // 게시글 내용

    private String writer;   // 작성자

    @Builder.Default
    private int file = 0; // 파일 0

    @Builder.Default
    private int hit = 0; // 조회수 처음에 0

    private String regIp; // 작성일시

    @CreationTimestamp
    private LocalDateTime regDate; // 작성일


}
