package DC_square.spring.web.dto.response.place;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PlacePageResponseDTO<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private boolean last;
    private boolean first;
    private int size;
    private int number;

    public static <T> PlacePageResponseDTO<T> of(List<T> content, int page, int size) {
        int totalElements = content.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        return PlacePageResponseDTO.<T>builder()
                .content(fromIndex < totalElements ? content.subList(fromIndex, toIndex) : new ArrayList<>())
                .totalPages((totalElements + size - 1) / size)
                .totalElements(totalElements)
                .last(toIndex >= totalElements)
                .first(page == 0)
                .size(size)
                .number(page)
                .build();
    }
}
