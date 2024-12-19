package BackAnt.controller.company;

import BackAnt.dto.CompanyDTO;
import BackAnt.dto.RequestDTO.CompanyRequestDTO;
import BackAnt.dto.ResponseDTO.ApiResponseDTO;
import BackAnt.entity.Company;
import BackAnt.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;
    private final ModelMapper modelMapper;

    @PostMapping("/insert")
    public ResponseEntity<?> addCompany(@RequestBody CompanyRequestDTO companyDTO) {
        try {
            log.info("회사 " + companyDTO.toString());
            System.out.println(companyDTO.toString() + "외사ㅏ");
            // 회사 생성
            Company savedCompany = companyService.createCompany(companyDTO);

            // 성공 응답
            return ResponseEntity.ok(
                    new ApiResponseDTO<>(true, "회사 저장 성공", savedCompany.getId())
            );
        } catch (Exception e) {
            // 실패 응답
            return ResponseEntity.status(500)
                    .body(new ApiResponseDTO<>(false, "회사 저장 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("select/{id}")
    public CompanyDTO selectCompany(@PathVariable Long id) {

        log.info("회사조회하기" + id);

        CompanyDTO dto = companyService.getCompanyById(id);
        log.info(dto);
        return dto;

    }

}
