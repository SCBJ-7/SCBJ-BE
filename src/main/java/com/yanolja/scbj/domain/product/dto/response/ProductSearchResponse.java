package com.yanolja.scbj.domain.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSearchResponse {
   @NotNull
   private Long id;
   private String name;
   private String roomType;
   private String imageUrl;
   private Integer originalPrice;
   private Integer salePrice;
   private Double salePercentage;
   private LocalDate checkIn;
   private LocalDate checkOut;

   @Builder
   @QueryProjection
   public ProductSearchResponse(Long id, String name, String roomType, String imageUrl,
                                Integer originalPrice, Integer salePrice, Double salePercentage,
                                LocalDate checkIn,
                                LocalDate checkOut) {
      this.id = id;
      this.name = name;
      this.roomType = roomType;
      this.imageUrl = imageUrl;
      this.originalPrice = originalPrice;
      this.salePrice = salePrice;
      this.salePercentage = salePercentage;
      this.checkIn = checkIn;
      this.checkOut = checkOut;
   }
}

