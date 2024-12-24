package BackAnt.entity.board;

import BackAnt.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/*
   날 짜 : 2024/12/02(월)
   담당자 : 김민희
   내 용 : Board 를 위한 Entity 생성

   수정내역 :
   2024/12/03(화) 김민희 - likes 컬럼 추가 (* sql 예약어 "like" 컬럼명 추가 x)
   2024/12/06(금) 김민희 - 게시글의 전체 좋아요 수(likes) 관리 하는 필드
                       - 좋아요 증가/감소
   2024/12/23(금) 김민희 - 카테고리 삭제

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
    private Long id; // 게시글 번호

    @Column(nullable = false, length = 100)   // NULL 불가, 최대 100자
    private String title;    // 게시글 제목

    @Builder.Default
    private int comment = 0; // 게시글 댓글 0

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;  // 게시글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    //@ToString.Exclude  // ToString 순환 참조 방지
    private User writer; // 게시글 작성자 (User Entity )속

    @Builder.Default
    private int file = 0; // 파일 처음에 0

    @Builder.Default
    private int hit = 0; // 조회수 처음에 0

    @Builder.Default
    private int likes = 0; // 좋아요 처음에 0 (전체 좋아요 수)

    private String regIp; // 작성자 IP

    @CreationTimestamp
    @Column(name = "reg_date", updatable = false)  // 날짜 한 번 저장 된 후 -> 수정 불가능
    private LocalDateTime regDate; // 날짜

    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate; // 글 수정된 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false) // 카테고리 연결
    private BoardCategory category;

    // 좋아요 증가
    public void increaseLikes() {
        this.likes++;
    }

    // 좋아요 취소
    public void decreaseLikes() {
        this.likes = Math.max(0, this.likes - 1);  // 음수 방지
    }

}