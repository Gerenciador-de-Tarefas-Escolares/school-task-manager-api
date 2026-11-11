package com.escolar.gerenciadortarefas.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Configuration
public class JwtKeyConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

     @Bean
    public Key jwtSigningKey() {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("A propriedade jwt.secret não foi carregada corretamente do arquivo .env");
        }
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
