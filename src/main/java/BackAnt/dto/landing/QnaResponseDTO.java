package BackAnt.dto.landing;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class QnaResponseDTO {

    private Long id;
    private String companyName; // 회사명
    private String businessType; // 업종
    private String name; // 이름
    private String contactNumber; // 연락처
    private String email; // 이메일=
    private String inquiryDetails; // 문의사항
    private String answer;

    private String createdAt;
    private String answeredAt;
}
