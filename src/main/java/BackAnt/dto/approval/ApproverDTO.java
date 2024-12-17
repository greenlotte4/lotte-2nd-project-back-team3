package BackAnt.dto.approval;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproverDTO {
    private Long id;
    private String name;
    private String position;
    private String status;
}