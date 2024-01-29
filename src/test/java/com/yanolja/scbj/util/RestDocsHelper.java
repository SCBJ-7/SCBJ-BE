package com.yanolja.scbj.util;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

import lombok.experimental.UtilityClass;
import org.springframework.restdocs.headers.AbstractHeadersSnippet;

@UtilityClass
public class RestDocsHelper {

    public AbstractHeadersSnippet jwtHeader() {
        return requestHeaders(headerWithName("Authorization").description("JWT Access Token"));

    }
}
