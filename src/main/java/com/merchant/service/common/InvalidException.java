package com.merchant.service.common;


import com.merchant.service.enumclass.ErrorCode;

public class InvalidException extends RuntimeException {
    ErrorCode errorCode;

    public InvalidException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }
}
