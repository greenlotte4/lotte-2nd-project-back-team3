package BackAnt.dto.drive;

import BackAnt.entity.DriveFileEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveShareAllViewDTO {

    private String driveFolderId; // String -> 랜덤값 생성

    private String driveFolderName;

    private String driveParentFolderId;

    private int driveFolderSize;

    private LocalDateTime driveFolderCreatedAt;

    private LocalDateTime driveFolderSharedAt;

    private int driveFolderShareType = 0;//(0이면 공유중아님, 1이면 공유중)

    private boolean driveFolderIsStared = false;//(0이면 즐찾X, 1이면 즐찾됨)

    private String driveFolderMaker; // 등록한 사람(공유폴더일시 수정한사람)

}
