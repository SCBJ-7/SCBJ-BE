package com.yanolja.scbj.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry(proxyTargetClass = true)
public class RetryConfig {

    public static final int MAX_ATTEMPTS = 2;
    public static final int MAX_DELAY = 500;

}
