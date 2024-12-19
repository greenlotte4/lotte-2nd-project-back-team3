package BackAnt.service;

import BackAnt.dto.CompanyDTO;
import BackAnt.dto.RequestDTO.CompanyRequestDTO;
import BackAnt.entity.Company;
import BackAnt.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    // 회사 생성
    public Company createCompany(CompanyRequestDTO companyDTO) {
        Company company = modelMapper.map(companyDTO, Company.class);
        return companyRepository.save(company);
    }

    // 회사 조회
    public CompanyDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("이 id의 회사가 없습니다."));

        log.info("Company found: " + company.toString());

        return modelMapper.map(company, CompanyDTO.class);
    }

}
