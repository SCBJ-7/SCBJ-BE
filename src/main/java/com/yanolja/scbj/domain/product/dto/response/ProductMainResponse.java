package com.yanolja.scbj.domain.product.dto.response;

import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

public record ProductMainResponse(
    List<CityResponse> seoul,
    List<CityResponse> gangwon,
    List<CityResponse> busan,
    List<CityResponse> jeju,
    List<CityResponse> gyeongsang,
    List<CityResponse> jeolla,
    Page<WeekendProductResponse> weekend
) {

    @Builder
    public ProductMainResponse {
    }
}
