package com.yanolja.scbj.global.helper;

public enum TestConstants {
    GRANT_TYPE("Bearer "),
    REFRESH_PREFIX("Refresh"),
    ACCESS_TOKEN("ACCESS_TOKEN");

    private final String value;

    TestConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
