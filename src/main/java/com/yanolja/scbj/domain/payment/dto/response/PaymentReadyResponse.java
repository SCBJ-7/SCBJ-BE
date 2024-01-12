package com.yanolja.scbj.domain.payment.dto.response;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PaymentReadyResponse{
    private String tid;
    private String next_redirect_pc_url;
    private String next_redirect_mobile_url;
    private String next_redirect_app_url;
    private String android_app_scheme;
    private String ios_app_scheme;
    private String created_at;

}
