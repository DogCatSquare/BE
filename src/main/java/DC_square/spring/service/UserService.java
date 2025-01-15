package DC_square.spring.service;

import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.UserRepository;
import DC_square.spring.web.dto.request.LoginRequestDto;
import DC_square.spring.web.dto.request.UserRequestDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto createUser(UserRequestDto request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        // RequestDto -> Entity
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword()) // 실제로는 암호화 필요
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .regionId(request.getRegionId())
                .build();

        // 저장
        User savedUser = userRepository.save(user);

        // Entity -> ResponseDto
        return UserResponseDto.from(savedUser);
    }

    public UserResponseDto login(LoginRequestDto request) {
        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        // 비밀번호 확인
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return UserResponseDto.from(user);
    }

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // ID로 유저 조회 메서드 추가
    public UserResponseDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return UserResponseDto.from(user);
    }
}