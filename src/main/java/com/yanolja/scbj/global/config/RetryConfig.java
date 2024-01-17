package com.yanolja.scbj.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry(proxyTargetClass = true)
public class RetryConfig {

    public static final int MAX_ATTEMPTS = 1;
    public static final int MAX_DELAY = 100;


}
