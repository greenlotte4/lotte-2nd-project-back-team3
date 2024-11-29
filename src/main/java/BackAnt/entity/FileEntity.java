package BackAnt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

/*
    날 짜 : 2024/11/28(목)
    담당자 : 황수빈
    내 용 : File 를 위한 Entity 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fid;

    private String oName; // 파일 기존 이름
    private String sName; // 파일 저장된 이름
    private String filePath; // 파일 경로
    private long fileSize; // 파일 크기


    @ColumnDefault("0") // 디폴트 값 - 삭제되지 않음
    private int isDeleted; //

//    @ManyToOne
//    @JoinColumn(name = "folder_id")
//    private Folder folder;  // 파일이 속한 폴더
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;


}
