package DC_square.spring.domain.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Coordinate {
    private Double latitude;
    private Double longitude;
    private Integer sequence;

    public Coordinate() {}

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
