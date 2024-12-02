package BackAnt.dto.RequestDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequestDTO {
    private String name;
    private String address;
    private String phone;
    private String logoUrl;
}
