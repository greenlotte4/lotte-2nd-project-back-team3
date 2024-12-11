package BackAnt.service.page;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.page.PageDTO;
import BackAnt.repository.mongoDB.page.PageRepository;
import BackAnt.repository.page.PageCollaboratorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
    날 짜 : 2024/11/29(금)
    담당자 : 황수빈
    내 용 : Page 를 위한 Service 생성
*/
// TODO : 현재 DTO 없이 Document 로 return 반환 수정해야함
@RequiredArgsConstructor
@Service
@Log4j2
public class PageService {

    private final PageRepository pageRepository;
    private final ModelMapper modelMapper;
    private final PageCollaboratorRepository pageCollaboratorRepository;
    private final MongoTemplate mongoTemplate;
    private final PageCollaboratorService pageCollaboratorService;
    // 사용자의 공유된 페이지 목록 조회
    public List<PageDTO> getSharedPages(String userId) {
        // 1. 사용자가 협업자로 등록된 페이지 ID 목록 조회
        List<String> sharedPageIds = pageCollaboratorService.getCollaboratedPageIds(userId);

        // 2. 페이지 ID 목록으로 실제 페이지 데이터 조회
        Query query = new Query(Criteria.where("_id").in(sharedPageIds)
                .and("deletedAt").is(null));  // 삭제되지 않은 페이지만 조회

        List<PageDocument> pages = mongoTemplate.find(query, PageDocument.class);

        // 3. PageDocument를 PageDTO로 변환
        return pages.stream()
                .map(page -> page.convertToDTO(page))
                .collect(Collectors.toList());
    }

    public PageDocument savePage(PageDTO page){
        PageDocument pageDocument = modelMapper.map(page, PageDocument.class);
        return pageRepository.save(pageDocument);
    }
    public PageDocument getPageById(String id) {
        return pageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found")); // ID로 페이지 조회
    }
    public List<PageDocument> getPageByUpdatedAt() {
     return pageRepository.findTop3ByDeletedAtIsNullOrderByUpdatedAtDesc();

    }
    public List<PageDocument> getPageByTemplate() {
        return pageRepository.findByIsTemplateTrue();

    }
    public List<PageDocument> getPagesByUid(String uid){
        return pageRepository.findByOwnerAndDeletedAtIsNull(uid); // 삭제되지 않은 게시물
    }
    public List<PageDocument> getDeletedPagesByUid(String uid){
        return pageRepository.findByOwnerAndDeletedAtIsNotNull(uid); // 삭제되지 않은 게시물
    }

    public String DeleteById(String id, String type){

        PageDocument page = pageRepository.findById(id).orElse(null); // ID로 페이지 조회

        if(page!=null){
            if(type.equals("soft")){
                page.setDeletedAt(LocalDateTime.now());
                pageRepository.save(page);
                return "softDelete Successfully";}
            if(type.equals("hard")){
                pageRepository.deleteById(id);
                return "hardDelete Successfully";
            }

        }

        return "failed to find page";

    }

    public void restorePage(String id) {
        PageDocument page = pageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found"));// deleted 필드를 false로 설정
        page.setDeletedAt(null); // 삭제 시간 정보 제거
        pageRepository.save(page);
    }

    // TODO : redis 임시 저장 후 DB 반영 되도록 수정할까 ?
    @Transactional
    public void updatePageInRealTime(PageDTO pageDTO) {
        try {
            PageDocument page = pageRepository.findById(pageDTO.get_id())
                    .orElseThrow(() -> new RuntimeException("Page not found"));

            // 내용 업데이트
            if (pageDTO.getContent() != null) {
                page.setContent(pageDTO.getContent());
            }
            if (pageDTO.getTitle() != null) {
                page.setTitle(pageDTO.getTitle());
            }

            page.setUpdatedAt(LocalDateTime.now());
            pageRepository.save(page);
            log.info("Page updated successfully via WebSocket");
        } catch (Exception e) {
            log.error("Error updating page via WebSocket: ", e);
            throw e;
        }
    }
    }
