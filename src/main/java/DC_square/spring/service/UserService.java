package DC_square.spring.service;


import DC_square.spring.config.S3.AmazonS3Manager;
import DC_square.spring.config.S3.Uuid;
import DC_square.spring.config.S3.UuidRepository;
import DC_square.spring.config.jwt.JwtTokenProvider;
import DC_square.spring.domain.entity.Dday;
import DC_square.spring.domain.entity.Pet;
import DC_square.spring.domain.entity.Region;
import DC_square.spring.domain.enums.DdayType;
import DC_square.spring.repository.dday.DdayRepository;
import DC_square.spring.domain.entity.User;
import DC_square.spring.repository.RegionRepository;
import DC_square.spring.repository.PetRepository;
import DC_square.spring.repository.community.UserRepository;
import DC_square.spring.web.dto.request.LoginRequestDto;
import DC_square.spring.web.dto.request.user.UserRegistrationRequestDto;  // DTO 변경
import DC_square.spring.web.dto.request.user.PetRegistrationDto;
import DC_square.spring.web.dto.response.PetResponseDto;
import DC_square.spring.web.dto.response.UserResponseDto;
import DC_square.spring.web.dto.response.user.LoginResponseDto;
import DC_square.spring.web.dto.response.user.UserInqueryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final PetRepository petRepository;
    private final DdayRepository ddayRepository;
    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    @Transactional
    public UserResponseDto createUser(UserRegistrationRequestDto request, MultipartFile profileImage, MultipartFile petImage) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        // 지역 정보 조회 또는 생성
        Region region = regionRepository.findByDoNameAndSiAndGu(
                request.getDoName(),
                request.getSi(),
                request.getGu()
        ).orElseGet(() -> { //조회 결과 없을 때 새롭게 생성 !
            Region newRegion = Region.builder()
                    .doName(request.getDoName())
                    .si(request.getSi())
                    .gu(request.getGu())
                    .build();
            return regionRepository.save(newRegion);
        });

        // 프로필 이미지 업로드
        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
            profileImageUrl = s3Manager.uploadFile(s3Manager.generateProfile(savedUuid), profileImage);
        }


        // RequestDto -> Entity, DB에 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 변화나
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .regionId(region.getId().toString())
                .adAgree(request.getAdAgree())
                .profileImageUrl(profileImageUrl)
                .build();

        // 저장
        User savedUser = userRepository.save(user);

        // 반려동물 저장
        String petImageUrl = null;
        if (petImage != null && !petImage.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
            petImageUrl = s3Manager.uploadFile(s3Manager.generatePet(savedUuid), petImage);
        }

        Pet pet = Pet.builder()
                .petName(request.getPet().getPetName())
                .dogCat(request.getPet().getDogCat())
                .breed(request.getPet().getBreed())
                .birth(request.getPet().convertBirthToLocalDate())
                .user(savedUser)
                .petImageUrl(petImageUrl)
                .build();

            petRepository.save(pet);


        // D-day 생성 - 사료 구매
        LocalDate foodDate = LocalDate.parse(request.getFoodDate());
        Dday foodDday = Dday.builder()
                .title("사료 구매")
                .day(foodDate.plusWeeks(request.getFoodDuring()))
                .term(request.getFoodDuring())
                .user(savedUser)
                .type(DdayType.FOOD)
                .build();
        foodDday.setDefaultImageUrl();
        ddayRepository.save(foodDday);

        // D-day 생성 - 패드/모래 구매
        LocalDate padDate = LocalDate.parse(request.getPadDate());
        Dday padDday = Dday.builder()
                .title("패드/모래 구매")
                .day(padDate.plusWeeks(request.getPadDuring()))
                .term(request.getPadDuring())
                .user(savedUser)
                .type(DdayType.PAD)
                .build();
        padDday.setDefaultImageUrl();
        ddayRepository.save(padDday);

        // D-day 생성 - 병원 방문일
        LocalDate hospitalDate = LocalDate.parse(request.getHospitalDate());
        Dday hospitalDday = Dday.builder()
                .title("병원 방문일")
                .day(hospitalDate)
                .user(savedUser)
                .type(DdayType.HOSPITAL)
                .build();
        hospitalDday.setDefaultImageUrl();
        ddayRepository.save(hospitalDday);

        // 토큰 생성
        String token = jwtTokenProvider.createToken(savedUser.getEmail());

        // 토큰을 포함한 응답 반환
        return UserResponseDto.from(savedUser, token);
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto request) {
        //이메일 여부 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
        // 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        // 토큰 생성
        String token = jwtTokenProvider.createToken(user.getEmail());

        return LoginResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userId(user.getId())
                .build();
    }

    // 이메일 중복 확인
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
    // 닉네임 중복  확인
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public UserResponseDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return UserResponseDto.from(user);
    }

    // 유저 조회
    @Transactional(readOnly = true)
    public UserInqueryResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 사용자의 regionId로 Region 정보 조회
        Region region = regionRepository.findById(Long.parseLong(user.getRegionId()))
                .orElseThrow(() -> new RuntimeException("지역 정보를 찾을 수 없습니다."));

        // 첫 번째 반려동물의 품종 가져오기
        String firstPetBreed = petRepository.findAllByUser(user).stream()
                .findFirst()
                .map(Pet::getBreed)
                .orElse(null);

        return UserInqueryResponseDto.fromUser(user, region, firstPetBreed);
    }

    // 반려동물 추가
    @Transactional
    public UserResponseDto addPet(Long userId, PetRegistrationDto request, MultipartFile petImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String petImageUrl = null;
        if (petImage != null && !petImage.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
            petImageUrl = s3Manager.uploadFile(s3Manager.generatePet(savedUuid), petImage);
        }

        // 반려동물 저장

        Pet pet = Pet.builder()
                .petName(request.getPetName())
                .dogCat(request.getDogCat())
                .breed(request.getBreed())
                .birth(request.convertBirthToLocalDate())
                .user(user)
                .petImageUrl(petImageUrl)
                .build();

        petRepository.save(pet);


        // 업데이트된 사용자 정보 반환
        return UserResponseDto.from(user);
    }

    // 반려동물 전체 조회
    @Transactional(readOnly = true)
    public List<PetResponseDto> getPets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 해당 사용자의 모든 반려동물 조회 후 DTO로 변환
        return petRepository.findAllByUser(user).stream()
                .map(PetResponseDto::from)
                .collect(Collectors.toList());
    }

    // 반려동물 상세조회
    @Transactional(readOnly = true)
    public PetResponseDto getPetDetail(Long userId, Long petId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("반려동물을 찾을 수 없습니다."));

        // 해당 반려동물이 요청한 사용자의 소유인지 확인
        if (!pet.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 반려동물의 정보를 조회할 권한이 없습니다.");
        }
        // 반려동물 정보 반환
        return PetResponseDto.from(pet);
    }



    // 반려동물 삭제
    @Transactional
    public void deletePet(Long userId, Long petId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("반려동물을 찾을 수 없습니다."));

        // 해당 반려동물이 요청한 사용자의 소유인지 확인
        if (!pet.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 반려동물을 삭제할 권한이 없습니다.");
        }

        // 반려동물 삭제
        petRepository.delete(pet);
    }

    //반려동물 수정
    @Transactional
    public PetResponseDto modifyPet(Long userId, Long petId, PetRegistrationDto request, MultipartFile petImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("반려동물을 찾을 수 없습니다."));

        // 해당 반려동물이 요청한 사용자의 소유인지 확인
        if (!pet.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 반려동물을 수정할 권한이 없습니다.");
        }

        String petImageUrl = pet.getPetImageUrl();
        if (petImage != null && !petImage.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder().uuid(uuid).build());
            petImageUrl = s3Manager.uploadFile(s3Manager.generatePet(savedUuid), petImage);
        }

        // 반려동물 정보 업데이트
        pet.setPetName(request.getPetName());
        pet.setDogCat(request.getDogCat());
        pet.setBreed(request.getBreed());
        pet.setBirth(request.convertBirthToLocalDate());
        pet.setPetImageUrl(petImageUrl);

        // 변경 사항 저장
        Pet updatedPet = petRepository.save(pet);

        // 수정된 반려동물 정보 반환
        return PetResponseDto.from(updatedPet);
    }
}