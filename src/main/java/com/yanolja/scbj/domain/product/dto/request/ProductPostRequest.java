package com.yanolja.scbj.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductPostRequest {

    @NotNull(message = "1차 양도 가격을 입력하세요.")
    private int firstPrice;
    @NotNull(message = "2차 양도 가격을 입력하세요.")
    private int secondPrice;
    @NotBlank(message = "정산 은행을 입력하세요.")
    private String bank;
    @NotBlank(message = "정산 계좌를 입력하세요.")
    private String accountNumber;
    @NotNull(message = "2차 양도 시간을 입력하세요.")
    private int secondGrantPeriod;

    @Builder
    public ProductPostRequest(int firstPrice, int secondPrice, String bank,
        String accountNumber, int secondGrantPeriod) {
        this.firstPrice = firstPrice;
        this.secondPrice = secondPrice;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.secondGrantPeriod = secondGrantPeriod;
    }
}
