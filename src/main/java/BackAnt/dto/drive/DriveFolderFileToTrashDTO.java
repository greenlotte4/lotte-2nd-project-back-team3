package BackAnt.dto.drive;


import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveFolderFileToTrashDTO {

    private List<String> driveFolderId;
    private List<Integer> selectedDriveFileIds;
}
