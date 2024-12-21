package BackAnt.dto.drive;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class MyDriveFileViewDTO {

    private int driveFileId;

    private String driveFileSName; // 파일 저장된 이름

    private double driveFileSize; // 파일 크기

    private boolean driveIsStarted;

    private String driveFolderId; // 본인의 폴더아이디에 담긴 파일(없을수도있음)

    private String driveFileMaker; // 등록한 사람

    private LocalDateTime driveFileCreatedAt;




}
