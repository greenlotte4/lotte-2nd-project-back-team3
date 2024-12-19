package BackAnt.controller;

import BackAnt.dto.VersionDTO;
import BackAnt.service.VersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping("/api/version")
public class VersionController {

    private final VersionService versionService;

    @GetMapping("/select")
    public VersionDTO selectVersion() {
        return versionService.selectVersion();
    }

}
