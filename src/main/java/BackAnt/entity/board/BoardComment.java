package BackAnt.entity.board;

import BackAnt.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
   날 짜 : 2024/12/18(수)
   담당자 : 김민희
   내 용 : Board Comment 를 위한 Entity 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "board_comment")
public class BoardComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 댓글번호
    // 기존의 id 컬럼을 기본 키로 유지

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Board board;  // 게시글과의 연관 관계

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User commentWriter;  // 댓글 작성자

    @Column(columnDefinition = "TEXT", nullable = false)
    private String commentContent;  // 댓글 내용

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_comment_id")
//    private BoardComment parentComment;  // 부모 댓글 (대댓글인 경우)
//
//    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
//    private List<BoardComment> childComments = new ArrayList<>();  // 자식 댓글 목록

    @Builder.Default
    private Boolean secret = false;  // 비밀 댓글 여부

    private String regIp;  // 작성자 IP

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;  // 작성일

    @UpdateTimestamp
    private LocalDateTime updatedAt;  // 수정일

    // 댓글 수정 메서드
    public void updateComment(String content,Boolean secret) {
        this.commentContent = content;
        this.secret = secret;
    }
//
//    // 대댓글 추가 메서드
//    public void addChildComment(BoardComment child) {
//        childComments.add(child);
//        child.setParentComment(this);
//    }

    // 데이터 유효성 검사를 위한 생성자
    public BoardComment(Board board, User user, String content, Boolean secret, String regIp) {
        if (board == null || board.getId() == null) {
            throw new IllegalArgumentException("게시글 정보가 유효하지 않습니다.");
        }
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("사용자 정보가 유효하지 않습니다.");
        }
        this.board = board;
        this.commentWriter = user;
        this.commentContent = content;
        this.secret = secret;
        this.regIp = regIp;
    }
}