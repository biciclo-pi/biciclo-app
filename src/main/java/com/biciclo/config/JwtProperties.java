package com.biciclo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriedades do JWT, bindadas a partir de app.jwt.* no application.yml.
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long expirationMinutes
) {
}
