package BackAnt.entity.board;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/*
   날 짜 : 2024/12/23(월)
   담당자 : 강은경
   내 용 : BoardCategory 를 위한 Entity 생성


*/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "BoardCategory")
public class BoardCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 카테고리 번호

    @Column(nullable = false) // 카테고리 이름
    private String name;

}
