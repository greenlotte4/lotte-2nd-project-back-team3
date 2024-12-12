package BackAnt.dto.RequestDTO;

import BackAnt.entity.Department;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequestDTO {

    private String name;
    private String uid;
    private String password;
    private String nick;
    private String phoneNumber;
    private String profileImageUrl; // 이미지 경로
    private MultipartFile profileImage; // 이미지 업로드를 위한 파일
    private Long tokenid;
    private String email;
    private String role;
    private String position;
    private String joinDate;

    private Department department;
}
