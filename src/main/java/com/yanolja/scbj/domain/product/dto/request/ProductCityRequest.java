package com.yanolja.scbj.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProductCityRequest(
    @NotNull
    List<String> cityNames
) {

}
