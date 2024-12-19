package BackAnt.service;

import BackAnt.dto.VersionDTO;
import BackAnt.entity.Version;
import BackAnt.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Service
public class VersionService {

    private final VersionRepository versionRepository;
    private final ModelMapper modelMapper;

    public VersionDTO selectVersion() {
        List<Version> versions = versionRepository.findAll();

        Version version = versions.get(0);

        return modelMapper.map(version, VersionDTO.class);

    }

}
