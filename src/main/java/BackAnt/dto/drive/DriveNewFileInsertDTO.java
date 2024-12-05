package BackAnt.dto.drive;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveNewFileInsertDTO {

    private int driveFileId;

    private List<MultipartFile> driveFiles;

    private String driveFileOName; // 파일 기존 이름
    private String driveFileSName; // 파일 저장된 이름
    private String driveFilePath; // 파일 경로
    private double driveFileSize; // 파일 크기
    private String driveFolderId; // 본인의 폴더아이디에 담긴 파일(없을수도있음)

    private String driveFileMaker; // 등록한 사람

    @CreationTimestamp
    private LocalDateTime driveFileCreatedAt;
    @CreationTimestamp
    private LocalDateTime driveFileUpdatedAt;
}
