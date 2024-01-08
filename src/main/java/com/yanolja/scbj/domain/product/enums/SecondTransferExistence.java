package com.yanolja.scbj.domain.product.enums;

public enum SecondTransferExistence {
    NOT_EXISTS(0);

    private final int status;

    SecondTransferExistence(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
