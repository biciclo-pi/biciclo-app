package com.biciclo.common.exception;

import java.time.Instant;

/**
 * Payload padronizado de erro (RNF05).
 * Sempre retornado com os campos: timestamp, status, error e message.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message
) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message);
    }
}
