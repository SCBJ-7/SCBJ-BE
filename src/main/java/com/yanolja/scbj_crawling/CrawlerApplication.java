package com.yanolja.scbj_crawling;

import com.yanolja.scbj_crawling.mockData.CreateMockData;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.yanolja.scbj", "com.yanolja.scbj_crawling"})
@RequiredArgsConstructor
public class CrawlerApplication {

    private final CreateMockData createMockData;

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }
}
