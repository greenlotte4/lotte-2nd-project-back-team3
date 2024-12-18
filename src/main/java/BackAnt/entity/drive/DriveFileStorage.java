package BackAnt.entity.drive;

import BackAnt.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "DriveFileStorage")
public class DriveFileStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int driveFileStorageId;

    @Column(nullable = false, unique = true)
    private String userId; // 소속된 사용자

    private long driveFileLimitSize;

    private String driveSize;

    private double driveFileSize;

}
