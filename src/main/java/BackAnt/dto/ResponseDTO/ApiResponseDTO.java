package BackAnt.dto.ResponseDTO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDTO<T> {
    private boolean success; // 성공 여부
    private String message;  // 응답 메시지
    private T data;          // 추가 데이터 (제네릭 사용)

    // 실패시 호출
    public static <T> ApiResponseDTO<T> fail(String message) {
        return new ApiResponseDTO<>(false, message, null);
    }
}
