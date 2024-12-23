package BackAnt.controller.page;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.page.PageCollaboratorDTO;
import BackAnt.dto.page.PageCreateDTO;
import BackAnt.dto.page.PageDTO;
import BackAnt.repository.page.PageCollaboratorRepository;
import BackAnt.service.page.PageCollaboratorService;
import BackAnt.service.page.PageImageService;
import BackAnt.service.page.PageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    private final PageCollaboratorService pageCollaboratorService;
    private final PageService pageService;
    private final PageImageService pageImageService;// MongoDB와 연결된 리포지토리
    private final SimpMessagingTemplate template;

    @PostMapping("/create")
    public ResponseEntity<?> createPage(@RequestBody PageCreateDTO page) {
        try {
            // 1. 기본 유효성 검사
            if (page == null || !isValidPageData(page)) {
                return ResponseEntity.badRequest()
                        .body("Invalid page data");
            }

            log.info("여기야 여기 pageCreateDTO : "+page);

            // 2. 페이지 수 제한 확인
            int pageCount = pageService.CountMyPages(page.getOwner());
            log.info("여기야 여기 이 사용자의 페이지 총 갯수 :"+pageCount);
            if (!page.getIsTemplate() && page.getCompanyRate() == 0 && pageCount >= 5) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Free member cannot create more than 5 pages");
            }

            // 3. 페이지 생성 및 소유자 설정
            PageDocument savedPage = pageService.savePage(page);
            pageCollaboratorService.addOwner(savedPage);

            // 4. 성공 응답
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedPage.get_id());

        } catch (Exception e) {
            log.error("Error creating page: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create page: " + e.getMessage());
        }
    }
    // 페이지 데이터에 owner이 있는지 확인
    private boolean isValidPageData(PageCreateDTO page) {
        return page.getOwner() != null && !page.getOwner().trim().isEmpty();
    }

    // 페이지 저장 - Template 저장에 이용
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
    public ResponseEntity<PageDTO> getPageById(@PathVariable String id) {
        PageDTO page = pageService.getPageById(id); // ID로 페이지 조회
        if (page == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 페이지가 없을 경우 404 반환
        }
        return ResponseEntity.ok(page); // 페이지가 존재할 경우 200 반환
    }

    @GetMapping("/shared/{userId}")
    public ResponseEntity<List<PageDTO>> getSharedPages(@PathVariable String userId) {
        List<PageDTO> sharedPages = pageService.getSharedPages(userId);
        return ResponseEntity.ok(sharedPages);
    }

    // page List 조회 ( DELETED | UID )
    @GetMapping("/list/{type}/{uid}")
    public ResponseEntity<List<PageDTO>> selectByUid(@PathVariable String type,
                                                          @PathVariable (required = false) String uid) {
        try {
            List<PageDTO> pages;

            switch (type) {
                case "deleted":
                    pages = pageService.getDeletedPagesByUid(uid);
                    break;
                case "uid":
                    pages = pageService.getPagesByUid(uid);
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

    @GetMapping("/template/list")
    public ResponseEntity<List<PageDTO>> selectTemplates() {

            log.info("컨트롤러 입성");
            List<PageDTO> pages = pageService.getPageByTemplate();
            log.info("pages"+pages);
            return ResponseEntity.ok()
                            .body(pages); // 빈 리스트 반환

    }

    // page 삭제 ( SOFT | HARD )
    @Transactional
    @DeleteMapping("/{id}/{type}")
    public String softDeletePageById(@PathVariable String id,@PathVariable String type) {
        if (type.equals("soft")) {
            return pageService.DeleteById(id, "soft");
        }
        if (type.equals("hard")){
            log.info("pageId로 협업자 삭제 시도 id - "+id);
            pageCollaboratorService.removeCollaboratorsByPageId(id);
            log.info("협업자 삭제 완료 ");
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

    ////////// 페이지 협업자 ///////////

    // 페이지 협업자 목록 조회
    @GetMapping("/{pageId}/collaborators")
    public ResponseEntity<List<PageCollaboratorDTO>> getCollaborators(@PathVariable String pageId) {
        List<PageCollaboratorDTO> collaborators = pageCollaboratorService.getCollaborators(pageId);
        return ResponseEntity.ok(collaborators);
    }

    //  페이지 협업자 추가
    @PostMapping("/{pageId}/collaborators")
    public ResponseEntity<List<PageCollaboratorDTO>> addCollaborators(
            @PathVariable String pageId,
            @RequestBody Map<String, List<PageCollaboratorDTO>> request) {
        log.info("페이지 협업자 추가 : "+request);
        List<PageCollaboratorDTO> collaborators = request.get("collaborators");
        List<PageCollaboratorDTO> addedCollaborators = pageCollaboratorService.addCollaborators(pageId, collaborators);
        return ResponseEntity.ok(addedCollaborators);
    }

    // 페이지 협업자 삭제
    @Transactional
    @DeleteMapping("/{pageId}/collaborators/{userId}")
    public ResponseEntity<Void> removeCollaborator(
            @PathVariable String pageId,
            @PathVariable long userId) {
        pageCollaboratorService.removeCollaborator(pageId, userId);
        return ResponseEntity.ok().build();
    }
    // 페이지 협업자 권한 업데이트
    @PutMapping("/{pageId}/collaborators/{userId}")
    public ResponseEntity<Void> updateCollaboratorPermission(
            @PathVariable String pageId,
            @PathVariable long userId,
            @RequestBody Map<String, Integer> request) {
        int permissionType = request.get("type");
        pageCollaboratorService.updateCollaboratorPermission(pageId, userId, permissionType);
        return ResponseEntity.ok().build();
    }
}