package DC_square.spring.service;

import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.request.UserRequestDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = User.builder()
                .email(userRequestDto.getEmail())
                .username(userRequestDto.getUsername())
                .password(userRequestDto.getPassword())
                .build();

        //saved to repository
        User savedUser = userRepository.save(user);

        //Entity to ResponseDTO
        return UserResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    //ID로 사용자 조회
    public UserResponseDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자 조회를 할 수 없습니다."));

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
