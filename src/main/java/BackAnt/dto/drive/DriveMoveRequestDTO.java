package BackAnt.dto.drive;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveMoveRequestDTO {
    //이동 할 폴더
    //이동 될 폴더

    private String driveFolderId;
    private String selectDriveFolderId;
    private int driveFileId;
    private String userId;

}
