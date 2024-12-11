package BackAnt.entity.board;

import BackAnt.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/*
   날 짜 : 2024/12/05(금)
   담당자 : 김민희
   내 용 : Board 를 위한 Entity 생성
         - 사용자별 좋아요 수 count 를 위한 DTO
         - 어떤 사용자(uid)가 어떤 게시글(boardId)에 좋아요를 눌렀는지"를 저장하는 테이블

   수정내역 :
*/

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board_like", uniqueConstraints = {@UniqueConstraint(columnNames = {"board_id", "user_id"})})
public class BoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 자체의 고유 번호

    @Column(name = "board_id")
    private Long boardId; // 좋아요가 눌린 게시글 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user; // 좋아요를 누른 사용자의 ID

    @Column(length = 20)
    private String nick;  // 좋아요 누른 사용자 닉네임 (조회 성능 개선용)

    private String regIp;  // Board 엔티티와 동일한 스타일로 IP 추가

    @CreationTimestamp
    private LocalDateTime regDate; // 좋아요가 등록된 시간


    public BoardLike(Long boardId, Long uid, String nick, String regIp, User user) {
        this.boardId = boardId;
        this.user = user;
        this.nick = nick;
        this.regIp = regIp;
    }
}
