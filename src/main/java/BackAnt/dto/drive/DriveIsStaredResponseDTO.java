package BackAnt.dto.drive;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveIsStaredResponseDTO {

    private String driveFolderId;
    private String userId;
    private int driveFileId;
    private boolean isStared;
}
