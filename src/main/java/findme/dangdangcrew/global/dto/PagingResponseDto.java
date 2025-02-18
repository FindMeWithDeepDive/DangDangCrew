package findme.dangdangcrew.global.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.query.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class PagingResponseDto {

    private Long pages;
    private Long total;
    private List<?> results;

    @Builder
    private PagingResponseDto(long pages, long total, List<?> results){
        this.pages = pages;
        this.total= total;
        this.results = results;
    }

    public static PagingResponseDto of(long pages, long total, List<?> results){
        return PagingResponseDto.builder()
                .pages(pages)
                .total(total)
                .results(results)
                .build();
    }
}
