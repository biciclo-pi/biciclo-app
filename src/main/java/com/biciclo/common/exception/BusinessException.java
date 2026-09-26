package com.biciclo.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção de negócio com código HTTP arbitrário.
 * Use para violações de regra (ex.: 422 no resgate de recompensa).
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message) {
        this(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
