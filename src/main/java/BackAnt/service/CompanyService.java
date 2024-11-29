package BackAnt.service;

import BackAnt.dto.CompanyDTO;
import BackAnt.entity.Company;
import BackAnt.repository.CompanyRepository;
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

    public Company insertCompany(CompanyDTO companyDTO) {
        // DTO -> Entity 변환
        Company company = modelMapper.map(companyDTO, Company.class);
        // 데이터 저장
        return companyRepository.save(company);
    }

}
