package BackAnt.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.*;
import java.time.LocalDateTime;

/*
   날 짜 : 2024/12/02(월)
   담당자 : 김민희
   내 용 : Board 를 위한 Entity 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Board")
public class Board {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no; // 게시글 번호

    private String cate1; // 카테고리1
    private String cate2; // 카테고리2

    @Column(nullable = false, length = 100)   // NULL 불가, 최대 100자
    private String title;    // 게시글 제목

    @Builder.Default
    private int comment = 0; // 게시글 댓글 0

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;  // 게시글 내용

    @ManyToOne(fetch = FetchType.LAZY) // 기본 지연 로딩 유지
    @JoinColumn(name="writer_id", referencedColumnName = "id", nullable = false)
    private User writer; // 게시글 작성자 (User Entity )

    @Builder.Default
    private int file = 0;

    @Builder.Default
    private int hit = 0; // 조회수 처음에 0

    private String regIp; // 작성일시

    @CreationTimestamp
    private LocalDateTime regDate; // 작성일


}