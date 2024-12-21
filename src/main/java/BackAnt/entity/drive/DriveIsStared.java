package BackAnt.entity.drive;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "DriveIsStared")
public class DriveIsStared {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int driveIsStaredId;

    private String userId;

    private String driveFolderId;

    private int driveFileId;

    private boolean isStared = false;
}
