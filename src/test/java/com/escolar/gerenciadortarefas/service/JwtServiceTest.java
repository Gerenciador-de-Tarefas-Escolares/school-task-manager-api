package com.escolar.gerenciadortarefas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secretKey",  "404E635266556A586e3272357538782F413f4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);

        userDetails = new User("rebeca@email.com", "senha123", new ArrayList<>());
    }

    @Test
    void deveGerarEExtrairUsernameDoToken() {
        String token =jwtService.gerarToken(userDetails);

        assertNotNull(token);
        String usernameExtraido = jwtService.extrairUsername(token);
        assertEquals("rebeca@email.com", usernameExtraido);
    }

    @Test
    void deveValidarTokenComSucesso() {
        String token = jwtService.gerarToken(userDetails);

        Boolean isValid = jwtService.validarToken(token, userDetails);
        assertTrue(isValid);
    }
}
