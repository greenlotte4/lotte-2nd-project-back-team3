package BackAnt.service;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.PageRequestDTO;
import BackAnt.repository.mongoDB.PageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/*
    날 짜 : 2024/11/29(금)
    담당자 : 황수빈
    내 용 : Page 를 위한 Service 생성
*/

@RequiredArgsConstructor
@Service
public class PageService {
    private final PageRepository pageRepository;
    private final ModelMapper modelMapper;

    public PageDocument savePage(PageRequestDTO page){
        return pageRepository.save(modelMapper.map(page, PageDocument.class));
    }
    public PageDocument getPageById(String id) {
        return pageRepository.findById(id).orElse(null); // ID로 페이지 조회
    }
    public List<PageDocument> getPagesByUid(String uid){
        return pageRepository.findByUid(uid);
    }
    public void deleteById(String id){
         pageRepository.deleteById(id);
    }
}
