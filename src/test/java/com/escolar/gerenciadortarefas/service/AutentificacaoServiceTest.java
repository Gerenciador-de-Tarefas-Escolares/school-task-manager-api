package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutentificacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutentificacaoService autentificacaoService;

    private Usuario usuario;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
        usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNome("Samantha");
        usuario.setEmail("samantha@email.com");
        usuario.setSenha(encoder.encode("senha123456"));
    }

    @Test
    void deveLancarExcecaoAoRegistrarEmailDuplicado() {
        when(usuarioRepository.findByEmail("samantha@email.com")).thenReturn(Optional.of(usuario));

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail("samantha@email.com");
        novoUsuario.setSenha("12345678");

        assertThrows(RuntimeException.class, () -> {
            autentificacaoService.registrar(novoUsuario);
        });

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveFazerLoginComSucesso() {
        when(usuarioRepository.findByEmail("samantha@email.com")).thenReturn(Optional.of(usuario));

        Usuario resultado = autentificacaoService.login("samantha@email.com", "senha123456");

        assertNotNull(resultado);
        assertEquals("samantha@email.com", resultado.getEmail());
    }

    @Test
    void deveLancarExcecaoParaSenhaIncorretaNoLogin() {
        when(usuarioRepository.findByEmail("samantha@email.com")).thenReturn(Optional.of(usuario));

        assertThrows(RuntimeException.class, () -> {
            autentificacaoService.login("samantha@email.com", "senhaErrada");
        });
    }
}
