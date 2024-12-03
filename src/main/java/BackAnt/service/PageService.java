package BackAnt.service;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.PageDTO;
import BackAnt.repository.mongoDB.PageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*
    날 짜 : 2024/11/29(금)
    담당자 : 황수빈
    내 용 : Page 를 위한 Service 생성
*/
// TODO : 현재 DTO 없이 Document 로 return 반환 수정해야함
@RequiredArgsConstructor
@Service
public class PageService {
    private final PageRepository pageRepository;
    private final ModelMapper modelMapper;

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
    public List<PageDocument> getPagesByUid(String uid){
        return pageRepository.findByUidAndDeletedAtIsNull(uid); // 삭제되지 않은 게시물
    }
    public List<PageDocument> getDeletedPagesByUid(String uid){
        return pageRepository.findByUidAndDeletedAtIsNotNull(uid); // 삭제되지 않은 게시물
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

}