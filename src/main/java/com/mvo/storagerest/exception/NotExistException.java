package com.mvo.storagerest.exception;

public class NotExistException extends ApiException {
    public NotExistException(String message, String errorCode) {
        super(message, errorCode);
    }
}
