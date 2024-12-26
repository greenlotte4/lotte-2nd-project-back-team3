package BackAnt.dto.drive;

import BackAnt.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveCollaboratorDTO {

    //공유드라이브 조회

    private int DriveFolderShareId;

    private String DriveFolderId;

    private int DriveShareType; //수정 , 읽기


    private LocalDateTime DriveFolderShareAt;

    private boolean isOwner; //  최고관리자인지 아닌지 (폴더 생성할때 만든사람이 최고관리자가 됨)

    private long user_id;
    private String uid;
    private String uidImage;
}
