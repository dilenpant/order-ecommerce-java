package com.order.ecommerce.exception;

public class OrderNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public ErrorList errors = new ErrorList();

    public OrderNotFoundException(final ErrorInfo error) {
        this.errors.addError(error);
    }

    public OrderNotFoundException(final ErrorInfo error, final Throwable cause) {
        super(cause);
        this.errors.addError(error);
    }
}