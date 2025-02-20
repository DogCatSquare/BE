package DC_square.spring.web.dto.response.walk;

import java.util.List;

public class GeocodingResponse {

    private String status;
    private List<Result> results;

    // Getter and Setter

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    // 내부 클래스: GeocodingResponse의 각 결과 객체
    public static class Result {
        private String formattedAddress;

        // Getter and Setter

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }
    }
}
