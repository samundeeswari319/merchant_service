package com.merchant.service.enumclass;

public enum StatusCode {
    SUCCESS(200),
    FAILURE(400),
    USER_NOT_FOUND(401),
    INTERNAL_SERVER_ERROR(500),
    FORBIDDEN(403);

    public int code;

    StatusCode(int code) {
        this.code = code;
    }
}

