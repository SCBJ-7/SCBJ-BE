package com.yanolja.scbj.domain.member.helper;

public enum TestConstants {
    GRANT_TYPE("Bearer "),
    REFRESH_PREFIX("Refresh");

    private String value;

    public String getValue() {
        return value;
    }

    TestConstants(String value) {
        this.value = value;
    }
}
