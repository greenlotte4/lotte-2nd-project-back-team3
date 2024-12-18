package BackAnt.entity.drive;

import BackAnt.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "DriveCollaborator")
public class DriveCollaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int driveFolderShareId;

    private String driveFolderId;

    private int driveShareType; //수정 , 읽기

    @CreationTimestamp
    private LocalDateTime driveFolderShareAt;

    private boolean isOwner; //  최고관리자인지 아닌지 (폴더 생성할때 만든사람이 최고관리자가 됨)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // 순환 참조 방지
    private User user; // 소속된 사용자

    private boolean isSharePoint;


}
