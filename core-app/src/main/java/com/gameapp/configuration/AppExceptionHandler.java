package com.gameapp.configuration;

import com.gameapp.core.util.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppException.class)
    protected ResponseEntity<Object> handleCustomException(AppException ex, WebRequest request) {
        log.error("Exception occurred", ex);
        return handleExceptionInternal(ex, ex, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }


    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        log.error("Exception occurred", ex);
        return handleExceptionInternal(ex, ex, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}

