package BackAnt.service.page;

import BackAnt.document.page.PageDocument;
import BackAnt.dto.page.PageCollaboratorDTO;
import BackAnt.dto.page.PageDTO;
import BackAnt.entity.User;
import BackAnt.entity.page.PageCollaborator;
import BackAnt.repository.UserRepository;
import BackAnt.repository.page.PageCollaboratorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PageCollaboratorService {
    private final ModelMapper modelMapper;


    private final PageCollaboratorRepository pageCollaboratorRepository;
    private final UserRepository userRepository;

    public PageCollaborator addOwner(PageDocument page){
        log.info("addOwner"+page);
        User user = userRepository.findByUid(page.getOwner())
                .orElseThrow(() -> new RuntimeException("User not found with uid: " + page.getOwner()));
       PageCollaborator owner = PageCollaborator.builder()
                .pageId(page.get_id())
                .user(user)
                .isOwner(true)
                .type(0)
                .invitedAt(page.getCreatedAt())
                .build();

        PageCollaborator savedOwner = pageCollaboratorRepository.save(owner);
        return savedOwner;
    }
    // 사용자가 협업자로 있는 페이지 목록 조회
    public List<String> getCollaboratedPageIds(String userId) {
        List<PageCollaborator> collaborators = pageCollaboratorRepository.findByUser_UidAndIsOwnerFalse(userId);
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
            log.info("협업자는 들어오나 ?"+collaboratorDTOs);
        List<PageCollaborator> newCollaborators = collaboratorDTOs.stream()
                .map(dto -> {
                    User user = userRepository.findById(dto.getUser_id())
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    return PageCollaborator.builder()
                            .pageId(pageId)
                            .user(user)
                            .type(dto.getType())
                            .isOwner(dto.getIsOwner())
                            .build();
                })
                .collect(Collectors.toList());

        List<PageCollaborator> savedCollaborators = pageCollaboratorRepository.saveAll(newCollaborators);
        return savedCollaborators.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 페이지 협업자 삭제
    public void removeCollaborator(String pageId, long userId) {
        pageCollaboratorRepository.deleteByPageIdAndUser_Id(pageId, userId);
    }
    public void removeCollaboratorsByPageId(String pageId) {
        pageCollaboratorRepository.deleteByPageId(pageId);
    }
    // 페이지 협업자 권한 업데이트
    public void updateCollaboratorPermission(String pageId, long userId, int permissionType) {
        PageCollaborator collaborator = pageCollaboratorRepository.findByPageIdAndUser_Id(pageId, userId)
                .orElseThrow(() -> new RuntimeException("Collaborator not found"));
        collaborator.setType(permissionType);
        pageCollaboratorRepository.save(collaborator);
    }

    // Entity를 DTO로 변환
    private PageCollaborator convertToEntity(PageCollaboratorDTO collaboratorDTO) {
        return PageCollaborator.builder()
                .id(collaboratorDTO.getId())
                .pageId(collaboratorDTO.getPageId())
                .user(userRepository.findById(collaboratorDTO.getUser_id()).orElseThrow())
                .invitedAt(collaboratorDTO.getInvitedAt())
                .isOwner(collaboratorDTO.getIsOwner())
                .type(collaboratorDTO.getType())
                .build();
    }
    // Entity를 DTO로 변환
    private PageCollaboratorDTO convertToDTO(PageCollaborator collaborator) {
        return PageCollaboratorDTO.builder()
                .id(collaborator.getId())
                .pageId(collaborator.getPageId())
                .user_id(collaborator.getUser().getId())
                .uid(collaborator.getUser().getUid())
                .name(collaborator.getUser().getName())
                .uidImage(collaborator.getUser().getProfileImageUrl())
                .invitedAt(collaborator.getInvitedAt())
                .isOwner(collaborator.getIsOwner())
                .type(collaborator.getType())
                .build();
    }
}
