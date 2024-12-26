package BackAnt.dto.drive;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriveFileStorageDTO {

    //드라이브 총 용량
    private long driveFileLimitSize; //총 사용가능 데이터 양
    private double driveFileSize; // 사용한 데이터 양



}
