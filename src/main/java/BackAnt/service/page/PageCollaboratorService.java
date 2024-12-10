package BackAnt.service.page;

import BackAnt.dto.page.PageCollaboratorDTO;
import BackAnt.entity.User;
import BackAnt.entity.page.PageCollaborator;
import BackAnt.repository.UserRepository;
import BackAnt.repository.page.PageCollaboratorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PageCollaboratorService {


    private final PageCollaboratorRepository pageCollaboratorRepository;
    private final UserRepository userRepository;

    // 사용자가 협업자로 있는 페이지 목록 조회
    public List<String> getCollaboratedPageIds(String userId) {
        List<PageCollaborator> collaborators = pageCollaboratorRepository.findByUser_Uid(userId);
        return collaborators.stream()
                .map(PageCollaborator::getPageId)
                .collect(Collectors.toList());
    }

    // 페이지 협업자 목록 조회
    public List<PageCollaboratorDTO> getCollaborators(String pageId) {

        List<PageCollaborator> collaborators = pageCollaboratorRepository.findByPageId(pageId);
        log.info("페이지 협업자 목록 : "+ collaborators);
        return collaborators.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 페이지 협업자 추가
    public List<PageCollaboratorDTO> addCollaborators(String pageId, List<PageCollaboratorDTO> collaboratorDTOs) {
        List<PageCollaborator> newCollaborators = collaboratorDTOs.stream()
                .map(dto -> {
                    User user = userRepository.findById(dto.getUser_id())
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    return PageCollaborator.builder()
                            .pageId(pageId)
                            .user(user)
                            .type(dto.getType())
                            .isOwner(dto.isOwner())
                            .build();
                })
                .collect(Collectors.toList());

        List<PageCollaborator> savedCollaborators = pageCollaboratorRepository.saveAll(newCollaborators);
        return savedCollaborators.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 페이지 협업자 삭제
    public void removeCollaborator(String pageId, String userId) {
        pageCollaboratorRepository.deleteByPageIdAndUser_Uid(pageId, userId);
    }

    // Entity를 DTO로 변환
    private PageCollaboratorDTO convertToDTO(PageCollaborator collaborator) {
        return PageCollaboratorDTO.builder()
                .id(collaborator.getId())
                .pageId(collaborator.getPageId())
                .user_id(collaborator.getUser().getId())
                .uidImage(collaborator.getUser().getProfileImageUrl())
                .invitedAt(collaborator.getInvitedAt())
                .isOwner(collaborator.isOwner())
                .type(collaborator.getType())
                .build();
    }
}