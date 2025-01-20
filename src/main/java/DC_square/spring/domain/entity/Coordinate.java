package DC_square.spring.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor  // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 초기화할 수 있는 생성자 추가
@Builder
public class Coordinate {

    private Double latitude;
    private Double longitude;
    private Integer sequence;

    // Getter와 Setter는 Lombok에서 자동으로 처리되므로 생략해도 됩니다.
    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
}
