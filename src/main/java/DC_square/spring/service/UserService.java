package DC_square.spring.service;


import DC_square.spring.domain.entity.Pet;
import DC_square.spring.domain.entity.Region;
import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.RegionRepository;
import DC_square.spring.repository.PetRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.request.LoginRequestDto;
import DC_square.spring.web.dto.request.user.UserRegistrationRequestDto;  // DTO 변경
import DC_square.spring.web.dto.request.user.PetRegistrationDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final PetRepository petRepository;

    @Transactional
    public UserResponseDto createUser(UserRegistrationRequestDto request) {  // DTO 타입 변경
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        // 지역 존재 여부 확인
        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 지역이 존재하지 않습니다."));

        // RequestDto -> Entity
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .regionId(region.getId().toString())
                .build();

        // 저장
        User savedUser = userRepository.save(user);

        // 여러 반려동물 정보 저장
        for (PetRegistrationDto petDto : request.getPets()) {
            Pet pet = Pet.builder()
                    .petName(petDto.getPetName())
                    .dogCat(petDto.getDogCat())
                    .breed(petDto.getBreed())
                    .birth(petDto.getBirthDate())
                    .user(savedUser)
                    .build();

            petRepository.save(pet);
        }

        return UserResponseDto.from(savedUser);
    }

    // 기존 메서드들은 그대로 유지
    public UserResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

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

    public UserResponseDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return UserResponseDto.from(user);
    }
}