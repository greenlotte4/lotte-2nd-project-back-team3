package BackAnt.dto.project;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
/*
    날 짜 : 2024/12/10(화)
    담당자 : 강은경
    내 용 : ProjectAssignedUserDTO 를 위한 DTO 생성(작업담당자 유저정보 담은 DTO)
*/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProjectAssignedUserDTO {

    private Long id; // 사용자 고유 ID
    private String name; // 사용자 이름
    private String uid; // 사용자 ID (고유)
    private String position; // 직위
    private String profileImageUrl; // 프로필 이미지 URL



}
