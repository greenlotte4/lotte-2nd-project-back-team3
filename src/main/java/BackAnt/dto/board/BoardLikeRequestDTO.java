package BackAnt.dto.board;

import BackAnt.entity.User;
import BackAnt.entity.board.BoardLike;
import lombok.*;

/*
    날 짜 : 2024/12/06(금)
    담당자 : 김민희
    내 용 : Board 를 위한 BoardLikeRequestDTO 생성
           - 글목록 상세 조회
           - 좋아요 기능에 대한 요청을 처리

*/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardLikeRequestDTO {

    private Long boardId;
    private Long uid;
    private String nick;
    private String regIp;


    public BoardLike toEntity(User user) {
        return BoardLike.builder()
                .boardId(this.boardId)
                .user(user)
                .nick(this.nick)
                .regIp(this.regIp)
                .build();
    }



}
