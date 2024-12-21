package BackAnt.dto.drive;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveIsStarredDTO {

    private String userId;

    private String driveFolderId;

    private int driveFileId;


}
