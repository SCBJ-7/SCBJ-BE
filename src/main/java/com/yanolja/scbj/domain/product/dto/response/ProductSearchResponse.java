package com.yanolja.scbj.domain.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
   private Boolean isFirstPrice;
   private Integer salePrice;
   private Double salePercentage;
   private LocalDate checkIn;
   private LocalDate checkOut;
   private LocalDateTime createdAt;
   private String reviewRate;
   private String hotelRate;



   @Builder
   @QueryProjection
   public ProductSearchResponse(Long id,
                                String name,
                                String roomType,
                                String imageUrl,
                                Integer originalPrice,
                                Boolean isFirstPrice,
                                Integer salePrice,
                                Double salePercentage,
                                LocalDate checkIn,
                                LocalDate checkOut,
                                LocalDateTime localDateTime,
                                String reviewRate,
                                String hotelRate) {
      this.id = id;
      this.name = name;
      this.roomType = roomType;
      this.imageUrl = imageUrl;
      this.originalPrice = originalPrice;
      this.isFirstPrice = isFirstPrice;
      this.salePrice = salePrice;
      this.salePercentage = salePercentage;
      this.checkIn = checkIn;
      this.checkOut = checkOut;
      this.createdAt = localDateTime;
      this.reviewRate = reviewRate;
      this.hotelRate = hotelRate;
   }


}

