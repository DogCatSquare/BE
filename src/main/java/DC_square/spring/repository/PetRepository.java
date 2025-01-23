package DC_square.spring.repository;

import DC_square.spring.domain.entity.Pet;
import DC_square.spring.domain.entity.User;
import DC_square.spring.domain.enums.DogCat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // 기본 CRUD 메서드는 JpaRepository에서 제공

    // 사용자별 반려동물 목록 조회
    List<Pet> findAllByUser(User user);
}