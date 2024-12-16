package BackAnt.dto.board;

import BackAnt.entity.User;
import BackAnt.entity.board.Board;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/*
    날 짜 : 2024/12/10(월)
    담당자 : 김민희
    내 용 : Board File 를 위한 BoardFileDTO 생성
           - 글쓰기 파일 업로드 및 글보기 파일 다운로드


*/
@Getter
@Setter
@ToString
public class BoardFileDTO {

    // 업로드 요청용 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UploadRequest {

        private Long boardId;
        private Long writerId;  // Board 객체를 직접 받음
        private Long writerName;  // Board 객체를 직접 받음
        private MultipartFile boardFile;    // User 객체를 직접 받음
        // MultipartFile은 여기서 제외 - @RequestPart로 별도 처리
    }

    // 업로드 응답용 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UploadResponse {
        private int boardFileId;
        private String boardFileOName;
        private double boardFileSize;
        private String boardFileExt;
        private LocalDateTime boardFileUploadedAt;
    }

    // 다운로드용 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DownloadInfoDTO {
        private String boardFileSName;
        private String boardFileOName;
    }

}
