package BackAnt.controller.page;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.page.PageDTO;
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

    @PostMapping("/create")
    public ResponseEntity<String> createPage(@RequestBody PageDTO page) {
        try {
            if (page == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid page data");
            }
            log.info("Received page data: " + page);
            PageDocument savedPage = pageService.savePage(page);
            log.info(savedPage.get_id());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedPage.get_id());
        } catch (Exception e) {
            log.error("Error saving page: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving page: " + e.getMessage());
        }
    }

    // 페이지 생성 및 수정
    @PostMapping("/save")
    public ResponseEntity<String> savePage(@RequestBody PageDTO page) {
        try {
            if (page == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid page data");
            }

            log.info("Received page data: " + page);
            PageDocument savedPage = pageService.savePage(page);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("success");
        } catch (Exception e) {
            log.error("Error saving page: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving page: " + e.getMessage());
        }
    }

    // 페이지 이미지 업로드
    @PostMapping("/upload") // image 업로드 후 url을 반환
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

    // page 조회
    @GetMapping("/{id}") // ID로 페이지 조회
    public ResponseEntity<PageDocument> getPageById(@PathVariable String id) {
        PageDocument page = pageService.getPageById(id); // ID로 페이지 조회
        if (page == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 페이지가 없을 경우 404 반환
        }
        return ResponseEntity.ok(page); // 페이지가 존재할 경우 200 반환
    }

    // page List 조회 (DELETED | MODIFIED | UID)
    @GetMapping("/list/{type}")
    public ResponseEntity<List<PageDocument>> selectByUid(@PathVariable String type) {
        try {
            String uid = "ghkdtnqls95";
            List<PageDocument> pages;

            switch (type) {
                case "deleted":
                    pages = pageService.getDeletedPagesByUid(uid);
                    break;
                case "uid":
                    pages = pageService.getPagesByUid(uid);
                    break;
                case "modified":
                    pages = pageService.getPageByUpdatedAt();
                    break;
                default: // type 오류
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(List.of()); // 빈 리스트 반환
            }

            if (pages.isEmpty()) { // page가 없는 경우
                return ResponseEntity.ok(pages); // 빈 리스트 반환
            }
            return ResponseEntity.ok(pages);

        } catch (Exception e) { // 에러 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of()); // 빈 리스트 반환
        }
    }

    // page 삭제 (SOFT | HARD)
    @DeleteMapping("/{id}/{type}")
    public String softDeletePageById(@PathVariable String id,@PathVariable String type) {
        if (type.equals("soft")) {
            return pageService.DeleteById(id, "soft");
        }
        if (type.equals("hard")){
            return pageService.DeleteById(id,"hard");
        }
        return "Deleted Failed...";
    }

    // page 복구
    @PutMapping("/{id}/restore")  // 페이지 복구 엔드포인트
    public ResponseEntity<String> restorePage(@PathVariable String id) {
        try {
            pageService.restorePage(id);
            return ResponseEntity.ok("Page restored successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to restore page");
        }
    }
}