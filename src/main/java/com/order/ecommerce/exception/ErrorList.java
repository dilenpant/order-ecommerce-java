package com.order.ecommerce.exception;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class ErrorList {
    private List<ErrorInfo> errors = new ArrayList();
    private String errorId = UUID.randomUUID().toString();

    public ErrorList() {
    }

    public void addError(final ErrorInfo error) {
        this.errors.add(error);
    }
}
