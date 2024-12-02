package BackAnt.controller.page;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.PageRequestDTO;
import BackAnt.service.PageImageService;
import BackAnt.service.PageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/*
    날 짜 : 2024/11/28(목)
    담당자 : 황수빈
    내 용 : Page 를 위한 Controller 생성
*/

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/page")
public class PageController {

    private final PageService pageService;
    private final PageImageService pageImageService;// MongoDB와 연결된 리포지토리
    private final ModelMapper modelMapper;

    // TODO : 현재는 버튼 클릭 시 저장 / 웹소켓으로 실시간 수정으로 바꿔야 함
    @PostMapping("/save")
    public ResponseEntity<String> savePage(@RequestBody PageRequestDTO page) {

        if (page == null || page.getTitle() == null || page.getContent() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid page data");
        }
        log.info("page"+page);
        PageDocument savedPage = pageService.savePage(page); // 페이지 저장
        return ResponseEntity.status(HttpStatus.CREATED) // 201 Created 상태 코드
                .body(savedPage.get_id());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = pageImageService.saveImage(file); // 서비스 호출
            log.info("image url - "+fileUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file.");
        }
    }

    @GetMapping("/{id}") // ID로 페이지 조회
    public ResponseEntity<PageDocument> getPageById(@PathVariable String id) {
        PageDocument page = pageService.getPageById(id); // ID로 페이지 조회
        if (page == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 페이지가 없을 경우 404 반환
        }
        return ResponseEntity.ok(page); // 페이지가 존재할 경우 200 반환
    }
//
//    @GetMapping("/list")
//    public ResponseEntity<List<PageDocument>> getListByUid
}