package BackAnt.service;

import BackAnt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/*
    날짜 : 2024/11/29
    이름 : 최준혁
    내용 : 유저 서비스 생성
*/

@RequiredArgsConstructor
@Log4j2
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
}
