package BackAnt.document.page.drive;


import BackAnt.entity.drive.DriveFileEntity;
import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/*
 날짜 : 2024.12.02
 이름 : 정지현
 내용 : folder 생성을 위한 document 생성

 */

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "DriveFolder")
public class DriveFolderDocument {

    @Id
    private String driveFolderId; // String -> 랜덤값 생성

    private String driveFolderName;

    private String driveParentFolderId;

    private List<DriveFileEntity> files;

    private int driveFolderSize;

    private String driveFolderPath;

    private LocalDateTime driveFolderCreatedAt;

    private LocalDateTime driveFolderUpdatedAt;

    private LocalDateTime driveFolderDeletedAt;

    private LocalDateTime driveFolderSharedAt;

    private int driveFolderShareType = 0;//(0이면 공유중아님, 1이면 공유중)

    private int driveFolderIsDeleted = 0;//(0이면 휴지통X, 1이면 휴지통)

    private boolean driveFolderIsStared = false;//(0이면 즐찾X, 1이면 즐찾됨)

    private String driveFolderMaker; // 등록한 사람(공유폴더일시 수정한사람)

    private String parentFolderName;

}
