package BackAnt.controller.board;

import BackAnt.dto.ResponseDTO.ApiResponseDTO;
import BackAnt.dto.board.BoardCategoryDTO;
import BackAnt.dto.board.comment.BoardCommentDTO;
import BackAnt.dto.board.comment.BoardCommentRequestDTO;
import BackAnt.dto.board.comment.BoardCommentUpdateDTO;
import BackAnt.service.board.BoardCategoryService;
import BackAnt.service.board.BoardCommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    날 짜 : 2024/12/23(월)
    담당자 : 강은경
    내 용 : BoardCategory 를 위한 Controller 생성
*/

@Log4j2
@RestController
@RequestMapping("/api/board/category")
@RequiredArgsConstructor
public class BoardCategoryController {


    private final BoardCategoryService boardCategoryService;

    // 게시판 카테고리 등록
    @PostMapping("/insert")
    public ResponseEntity<BoardCategoryDTO> createCategory(@RequestBody BoardCategoryDTO dto) {
        try {
            // 카테고리 생성
            BoardCategoryDTO createdCategory = boardCategoryService.createCategory(dto);

            // 성공적으로 생성된 DTO 반환
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            // 예외 발생 시 클라이언트에 BAD_REQUEST 상태 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 모든 카테고리 조회
    @GetMapping("/all")
    public ResponseEntity<List<BoardCategoryDTO>> getAllCategories() {
        List<BoardCategoryDTO> categories = boardCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // 특정 카테고리 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardCategoryDTO> getCategoryById(@PathVariable Long id) {
        BoardCategoryDTO category = boardCategoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    // 게시판 카테고리 수정
    @PutMapping("update/{id}")
    public ResponseEntity<BoardCategoryDTO> updateCategory(@PathVariable Long id, @RequestBody BoardCategoryDTO dto) {
        BoardCategoryDTO updatedCategory = boardCategoryService.updateCategory(id, dto);
        return ResponseEntity.ok(updatedCategory);
    }

    // 게시판 카테고리 삭제
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            boardCategoryService.deleteCategory(id);
            return ResponseEntity.ok("카테고리가 성공적으로 삭제되었습니다. ID: " + id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}