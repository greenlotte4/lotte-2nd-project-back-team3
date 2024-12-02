package BackAnt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/*
    날 짜 : 2024/11/28(목)
    담당자 : 황수빈
    내 용 : File 를 위한 Entity 생성
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "Files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileId;

    private String oName; // 파일 기존 이름
    private String sName; // 파일 저장된 이름
    private String filePath; // 파일 경로
    private long fileSize; // 파일 크기

    @ColumnDefault("0") // 디폴트 값 - 삭제되지 않음
    private int isDeleted; //

    @ColumnDefault("0") // 디폴트 값 - 즐찾되지 않음
    private int isFavorite;

    @ColumnDefault("0") // (0이면 공유중아님, 1이면 공유중)
    private int shareType;

    private String folderId; // 본인의 폴더아이디에 담긴 파일

    private String maker; // 등록한 사람

    @CreationTimestamp
    private LocalDateTime createdAt;
    @CreationTimestamp
    private LocalDateTime updatedAt;


}
