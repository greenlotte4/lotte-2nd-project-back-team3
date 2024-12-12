package BackAnt.entity.board;

import BackAnt.dto.board.BoardFileDTO;
import BackAnt.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/*
   날 짜 : 2024/12/10(화)
   담당자 : 김민희
   내 용 : Board File 를 위한 Entity 생성

   수정내역 :

*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "board_file")
public class BoardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardFileId; // 파일 고유 번호

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Board board;     // 게시글과의 연관 관계

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User boardFileMaker; // 파일 등록한 사용자

    @Column(nullable = false)
    private String boardFileOName; // 원본 파일명

    @Column(nullable = false)
    private String boardFileSName; // 저장된 파일명

    @Column(nullable = false)
    private String boardFilePath; // 파일 경로

    private double boardFileSize; // 파일 크기
    private String boardFileExt;  // 파일 확장자 (extension)
    private String boardFolderId; // 본인의 폴더아이디에 담긴 파일(선택적)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime boardFileCreatedAt; // 생성일시

    @UpdateTimestamp
    private LocalDateTime boardFileUpdatedAt; // 수정일시

    @CreationTimestamp
    private LocalDateTime boardFileUploadedAt; // 파일 업로드 일시

    // 데이터 유효성 검사
    public BoardFile(Board board, User user, String originalName,
                     String savedName, String path, double size, String ext) {
        if (board == null || board.getId() == null) {
            throw new IllegalArgumentException("게시글 정보가 유효하지 않습니다.");
        }
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("사용자 정보가 유효하지 않습니다.");
        }
        this.board = board;
        this.boardFileMaker = user;
        this.boardFileOName = originalName;
        this.boardFileSName = savedName;
        this.boardFilePath = path;
        this.boardFileSize = size;
        this.boardFileExt = ext;
    }

    // 파일 정보 업데이트 메서드
    public void updateFileInfo(String originalName, String savedName,
                               String path, long size, String ext) {
        this.boardFileOName = originalName;
        this.boardFileSName = savedName;
        this.boardFilePath = path;
        this.boardFileSize = size;
        this.boardFileExt = ext;
    }

}
