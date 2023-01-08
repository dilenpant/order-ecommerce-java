package com.order.ecommerce.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class OrderEcommerceGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    protected static final String ERROR_MESSAGE = "[{}] with error Id, error code and message [{}]";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception) {
        log.error(ERROR_MESSAGE, exception.getMessage());
        final var error = new ErrorInfo(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), exception.getMessage(), "", null);
        final var errorList = new ErrorList();
        errorList.addError(error);
        return new ResponseEntity<>(String.valueOf(errorList), getContentTypeHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Object> handleOrderNotFoundException(OrderNotFoundException exception) {
        log.error(ERROR_MESSAGE, exception.errors.getErrors());
        final var error = new ErrorInfo(String.valueOf(HttpStatus.NOT_FOUND.value()), "Order Not Found", "", exception.errors.getErrors().toArray());
        final var errorList = new ErrorList();
        errorList.addError(error);
        return new ResponseEntity<>(String.valueOf(errorList), getContentTypeHeaders(), HttpStatus.NOT_FOUND);
    }
    protected HttpHeaders getContentTypeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
