package BackAnt.dto.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseDTO<T> {
    private int status;        // HTTP 상태 코드
    private String message;    // 응답 메시지
    private T data;            // 응답 데이터

    // 성공 응답을 생성하는 메서드
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(200, "Success", data);
    }

    public static <T> ResponseDTO<T> success(int status, T data) {
        return new ResponseDTO<>(status, "Success", data);
    }


    // 실패 응답을 생성하는 메서드
    public static <T> ResponseDTO<T> failure(String message) {
        return new ResponseDTO<>(500, message, null);
    }

    public static <T> ResponseDTO<T> failure(int status, String message) {
        return new ResponseDTO<>(status, message, null);
    }

    // 생성자 및 Getter/Setter
    public ResponseDTO(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

