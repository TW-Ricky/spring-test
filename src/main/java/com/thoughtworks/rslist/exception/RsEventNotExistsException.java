package com.thoughtworks.rslist.exception;

public class RsEventNotExistsException extends RuntimeException {
    private String message;

    public RsEventNotExistsException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
