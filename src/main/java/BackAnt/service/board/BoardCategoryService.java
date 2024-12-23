package BackAnt.service.board;

import BackAnt.JWT.JwtProvider;
import BackAnt.dto.board.BoardCategoryDTO;
import BackAnt.dto.board.BoardDTO;
import BackAnt.dto.board.BoardResponseViewDTO;
import BackAnt.entity.User;
import BackAnt.entity.board.Board;
import BackAnt.entity.board.BoardCategory;
import BackAnt.entity.board.BoardLike;
import BackAnt.repository.UserRepository;
import BackAnt.repository.board.BoardCategoryRepository;
import BackAnt.repository.board.BoardLikeRepository;
import BackAnt.repository.board.BoardRepository;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/*
    날 짜 : 2024/12/23(월)
    담당자 : 강은경
    내 용 : BoardCategory 를 위한 Service 생성
*/

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class BoardCategoryService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BoardCategoryRepository boardCategoryRepository;
    private final BoardRepository boardRepository;

    // 게시판 카테고리 등록
    public BoardCategoryDTO createCategory(BoardCategoryDTO boardCategoryDTO) {

        BoardCategory boardCategory = modelMapper.map(boardCategoryDTO, BoardCategory.class);
        BoardCategory savedCategory = boardCategoryRepository.save(boardCategory);

        return modelMapper.map(savedCategory, BoardCategoryDTO.class);
    }

    // 모든 카테고리 조회
    public List<BoardCategoryDTO> getAllCategories() {
        return boardCategoryRepository.findAll()
                .stream()
                .map(category -> modelMapper.map(category, BoardCategoryDTO.class))
                .collect(Collectors.toList());
    }

    // 특정 카테고리 조회
    public BoardCategoryDTO getCategoryById(Long id) {
        BoardCategory boardCategory = boardCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. ID: " + id));

        return modelMapper.map(boardCategory, BoardCategoryDTO.class);
    }

    // 게시판 카테고리 수정
    public BoardCategoryDTO updateCategory(Long id, BoardCategoryDTO dto) {
        BoardCategory boardCategory = boardCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다. ID: " + id));

        boardCategory.setName(dto.getName());

        BoardCategory updatedCategory = boardCategoryRepository.save(boardCategory);
        return modelMapper.map(updatedCategory, BoardCategoryDTO.class);
    }

    // 게시판 카테고리 삭제
    public void deleteCategory(Long id) {
        if (!boardCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException("카테고리를 찾을 수 없습니다. ID: " + id);
        }

        // 해당 카테고리에 속한 게시글 삭제
        boardRepository.deleteByCategoryId(id);

        // 카테고리 삭제
        boardCategoryRepository.deleteById(id);
    }



}
