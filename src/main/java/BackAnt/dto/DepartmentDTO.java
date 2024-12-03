package BackAnt.dto;

import BackAnt.entity.Company;
import BackAnt.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentDTO {

    private Long id; // 부서 고유 ID
    private String name; // 부서명
    private Long company_id; // 소속 회사
    private List<User> users = new ArrayList<>(); // 부서에 속한 사용자들

}
