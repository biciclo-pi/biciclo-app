package com.biciclo.security;

import com.biciclo.common.enums.Role;
import com.biciclo.config.JwtProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET =
            "chave-de-teste-bem-longa-com-mais-de-32-bytes-para-hs256";

    private final JwtService jwtService =
            new JwtService(new JwtProperties(SECRET, 60));

    @Test
    void geraTokenValidoComClaims() {
        String token = jwtService.generateToken(1L, "ana@biciclo.com", Role.CYCLIST);

        assertTrue(jwtService.isValid(token));

        Claims claims = jwtService.parse(token);
        assertEquals("1", claims.getSubject());
        assertEquals("ana@biciclo.com", claims.get("email"));
        assertEquals("CYCLIST", claims.get("role"));
    }
}
