package BackAnt.service.page;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.page.PageCreateDTO;
import BackAnt.dto.page.PageDTO;
import BackAnt.entity.page.PageCollaborator;
import BackAnt.repository.mongoDB.page.PageRepository;
import BackAnt.repository.page.PageCollaboratorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static BackAnt.document.page.PageDocument.convertToDTO;

/*
    날 짜 : 2024/11/29(금)
    담당자 : 황수빈
    내 용 : Page 를 위한 Service 생성
*/
// TODO : 현재 DTO 없이 Document 로 return 반환 수정해야함
// 예외 처리를 추가한 PageService 클래스
@RequiredArgsConstructor
@Service
@Log4j2
public class PageService {

    private final PageRepository pageRepository;
    private final ModelMapper modelMapper;
    private final PageCollaboratorRepository pageCollaboratorRepository;
    private final MongoTemplate mongoTemplate;
    private final PageCollaboratorService pageCollaboratorService;
    private final SimpMessagingTemplate template;

    // 페이지 저장 ( 매개변수를 여러 종류로 받고 싶다면 - 오버로딩 또는 제네릭 타입 이용 )
    public <T> PageDocument savePage(T page) {
        try {
            PageDocument pageDocument = modelMapper.map(page, PageDocument.class);
            return pageRepository.save(pageDocument);
        } catch (Exception e) {
            log.error("Error saving page: {}", page, e);
            throw new RuntimeException("Failed to save page", e);
        }
    }
    // pageId로 Page 조회
    public PageDTO getPageById(String id) {
        try {
            PageDocument pageDocument = pageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Page not found with ID: " + id));
            return convertToDTO(pageDocument);
        } catch (Exception e) {
            log.error("Error fetching page by ID: {}", id, e);
            throw new RuntimeException("Failed to fetch page", e);
        }
    }
    // Uid 로 페이지 조회 - 삭제되지 않고 템플릿이 아닌 목록
    public List<PageDTO> getPagesByUid(String uid) {
        try {
            List<PageDocument> pageDocuments =  pageRepository.findByOwnerAndDeletedAtIsNullAndIsTemplateFalse(uid);

            return pageDocuments.stream()
                    .map(PageDocument::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching pages for user: {}", uid, e);
            throw new RuntimeException("Failed to fetch pages for user", e);
        }
    }
    // 공유된 페이지 목록
    public List<PageDTO> getSharedPages(String userId) {
        try {
            List<String> sharedPageIds = pageCollaboratorService.getCollaboratedPageIds(userId);

            if (sharedPageIds == null || sharedPageIds.isEmpty()) {
               log.info("공유된 페이지가 없습니다.");
            }

            Query query = new Query(Criteria.where("_id").in(sharedPageIds)
                    .and("deletedAt").is(null));

            List<PageDocument> pages = mongoTemplate.find(query, PageDocument.class);

            return pages.stream()
                    .map(PageDocument::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching shared pages for user: {}", userId, e);
            throw new RuntimeException("Failed to fetch shared pages", e);
        }
    }
    // Template 목록
    public List<PageDTO> getPageByTemplate() {
        try {
             List<PageDocument> pageDocuments = pageRepository.findByIsTemplateTrue();

            return pageDocuments.stream()
                    .map(PageDocument::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching template pages", e);
            throw new RuntimeException("Failed to fetch template pages", e);
        }
    }
    // 삭제된 페이지 조회
    public List<PageDTO> getDeletedPagesByUid(String uid) {
        try {
            List<PageDocument> pageDocuments = pageRepository.findByOwnerAndDeletedAtIsNotNullAndIsTemplateFalse(uid);

            return pageDocuments.stream()
                    .map(PageDocument::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching deleted pages for user: {}", uid, e);
            throw new RuntimeException("Failed to fetch deleted pages for user", e);
        }
    }
    // 페이지 삭제
    public String DeleteById(String id, String type) {
        try {
            PageDocument page = pageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Page not found with ID: " + id));

            if ("soft".equals(type)) {
                page.setDeletedAt(LocalDateTime.now());
                pageRepository.save(page);
                template.convertAndSend("/topic/page/aside", Map.of("_id", id, "deleted", true));
                return "softDelete Successfully";
            } else if ("hard".equals(type)) {
                pageRepository.deleteById(id);
                return "hardDelete Successfully";
            } else {
                throw new IllegalArgumentException("Invalid delete type: " + type);
            }
        } catch (Exception e) {
            log.error("Error deleting page with ID: {}", id, e);
            throw new RuntimeException("Failed to delete page", e);
        }
    }
    // 페이지 복구
    public void restorePage(String id) {
        try {
            PageDocument page = pageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Page not found with ID: " + id));
            page.setDeletedAt(null);
            pageRepository.save(page);
        } catch (Exception e) {
            log.error("Error restoring page with ID: {}", id, e);
            throw new RuntimeException("Failed to restore page", e);
        }
    }
    // 페이지 수정 시 웹소켓 방송 및 데이터 저장
    @Transactional
    public void updatePageInRealTime(PageDTO pageDTO) {
        try {
            PageDocument page = pageRepository.findById(pageDTO.get_id())
                    .orElseThrow(() -> new RuntimeException("Page not found with ID: " + pageDTO.get_id()));

            if (pageDTO.getContent() != null) {
                page.setContent(pageDTO.getContent());
            }
            if (pageDTO.getTitle() != null) {
                page.setTitle(pageDTO.getTitle());
                template.convertAndSend("/topic/page/aside", pageDTO);
            }

            page.setUpdatedAt(LocalDateTime.now());
            pageRepository.save(page);
            log.info("Page updated successfully via WebSocket");
        } catch (Exception e) {
            log.error("Error updating page via WebSocket for ID: {}", pageDTO.get_id(), e);
            throw new RuntimeException("Failed to update page in real-time", e);
        }
    }
    // 나의 페이지 갯수 세기
    public int CountMyPages(String owner) {
        return pageRepository.countAllByOwnerAndIsTemplateFalse(owner);
    }

}