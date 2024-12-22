package BackAnt.dto.drive;


import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveShareSelectFilesViewDTO {

    private int driveFileId;

    private String driveFileSName; // 파일 저장된 이름
    private String driveFilePath; // 파일 경로
    private double driveFileSize; // 파일 크기

    // 디폴트 값 - 즐찾되지 않음
    private boolean driveIsStarted;
    // (0이면 공유중아님, 1이면 공유중)
    private int driveShareType;

    private String driveFolderId; // 본인의 폴더아이디에 담긴 파일(없을수도있음)

    private String driveFileMaker; // 등록한 사람

    private LocalDateTime driveFileCreatedAt;

    private LocalDateTime driveFileUpdatedAt;

    private LocalDateTime driveFileSharedAt;

}
