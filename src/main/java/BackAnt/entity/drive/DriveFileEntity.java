package BackAnt.entity.drive;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
@Table(name = "DriveFiles")
public class DriveFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int driveFileId;

    private String driveFileOName; // 파일 기존 이름
    private String driveFileSName; // 파일 저장된 이름
    private String driveFilePath; // 파일 경로
    private double driveFileSize; // 파일 크기

    @ColumnDefault("0") // 디폴트 값 - 삭제되지 않음
    private int driveIsDeleted; //

    @ColumnDefault("0") // 디폴트 값 - 즐찾되지 않음
    private int driveIsStarted;

    @ColumnDefault("0") // (0이면 공유중아님, 1이면 공유중)
    private int driveShareType;

    private String driveFolderId; // 본인의 폴더아이디에 담긴 파일(없을수도있음)

    private String driveFileMaker; // 등록한 사람

    @CreationTimestamp
    private LocalDateTime driveFileCreatedAt;
    @CreationTimestamp
    private LocalDateTime driveFileUpdatedAt;

    private LocalDateTime driveFileDeletedAt;

    private LocalDateTime driveFileSharedAt;

    private String parentFolderName;


}
