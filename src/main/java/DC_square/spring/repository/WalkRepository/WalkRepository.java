package DC_square.spring.repository;

import DC_square.spring.domain.entity.Walk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalkRepository extends JpaRepository<Walk, Long> {

    // 특정 위도와 경도를 기준으로 가까운 산책로를 찾는 메서드
    @Query("SELECT w FROM Walk w WHERE ST_Distance_Sphere(point(w.longitude, w.latitude), point(:longitude, :latitude)) < :radius")
    List<Walk> findNearbyWalks(@Param("latitude") Double latitude,
                               @Param("longitude") Double longitude);

    // 산책로 제목으로 존재 여부 확인
    boolean existsByTitle(String title);
}
