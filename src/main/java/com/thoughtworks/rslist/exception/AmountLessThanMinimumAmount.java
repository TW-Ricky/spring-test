package com.thoughtworks.rslist.exception;

public class AmountLessThanMinimumAmount extends RuntimeException {
    private String message;

    public AmountLessThanMinimumAmount(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
