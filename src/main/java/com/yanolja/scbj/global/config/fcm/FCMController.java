package com.yanolja.scbj.global.config.fcm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/fcm")
public class FCMController {

    @GetMapping
    public String fcmTestPage() {
        return "FcmTest";
    }
}
