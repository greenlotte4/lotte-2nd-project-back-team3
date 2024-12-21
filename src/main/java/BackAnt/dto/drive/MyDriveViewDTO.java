package BackAnt.dto.drive;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyDriveViewDTO {

    private String driveFolderId; // String -> 랜덤값 생성

    private String driveFolderName;

    private String driveParentFolderId;

    private String driveFolderMaker; // 등록한 사람(공유폴더일시 수정한사람)

    private int driveFolderSize;
    private LocalDateTime driveFolderCreatedAt;
    private int driveFolderShareType;
    private boolean driveFolderIsStared;
}
