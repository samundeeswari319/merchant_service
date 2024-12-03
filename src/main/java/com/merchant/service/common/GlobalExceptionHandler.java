package com.merchant.service.common;

import com.merchant.service.enumclass.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity handleException(Exception e) {
        APIResponse response = new APIResponse();
        ErrorResponses errorResponse = new ErrorResponses(ErrorCode.INTERNAL_SERVER_ERROR);
        errorResponse.additionalInfo.excepCode = ErrorCode.INTERNAL_SERVER_ERROR.code;
        errorResponse.additionalInfo.excepText = e.getMessage();
        response.setError(errorResponse);
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(response);
    }

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity handleBadRequestException(InvalidException e) {
        APIResponse response = new APIResponse();
        ErrorResponses errorResponse = new ErrorResponses(e.errorCode);
        response.setError(errorResponse);
        response.setCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    /*@ExceptionHandler
    public ResponseEntity handleAccessDeniedException(AccessDeniedException e) {
        APIResponse response = new APIResponse();
        ErrorResponses errorResponse = new ErrorResponses(ErrorCode.MISSING_TOKEN);
        errorResponse.additionalInfo.excepCode = "1008";
        errorResponse.additionalInfo.excepText = e.getMessage();
        response.setError(errorResponse);
        response.setCode(HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(response.getCode()).body(response);
    }*/

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIResponse> handleAccessDeniedException(AccessDeniedException e) {
        APIResponse response = new APIResponse();
//        response.setCode(HttpStatus.UNAUTHORIZED.value());
        ErrorResponses errorResponse = new ErrorResponses(ErrorCode.MISSING_TOKEN);
        errorResponse.additionalInfo.excepCode = ErrorCode.MISSING_TOKEN.code;
        errorResponse.additionalInfo.excepText = e.getMessage();
        response.setCode(HttpStatus.FORBIDDEN.value());  // Change to 403 Forbidden
        response.setError(errorResponse); // Optionally, add a message
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

}
