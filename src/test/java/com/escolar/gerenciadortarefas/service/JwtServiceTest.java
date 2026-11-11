package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UsuarioUserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Sofia");
        usuario.setEmail("sofia@teste.com");
        usuario.setSenha("123456");

        userDetails = new UsuarioUserDetails(usuario);
    }

    @Test
    void deveGerarEValidarTokenCorretamente() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("sofia@teste.com", jwtService.extrairUsername(token));
        assertTrue(jwtService.validarToken(token, userDetails));
    }

    @Test
    void deveInvalidarTokenParaOutroUsuario() {
        String token = jwtService.generateToken(userDetails);

        Usuario outroUsuario = new Usuario();
        outroUsuario.setEmail("outro@teste.com");
        outroUsuario.setSenha("123456");
        UsuarioUserDetails outroUserDetails = new UsuarioUserDetails(outroUsuario);

        assertFalse(jwtService.validarToken(token, outroUserDetails));
    }
}
