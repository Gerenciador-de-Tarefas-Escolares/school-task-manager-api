package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.RecuperarSenhaDTO;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.UsuarioRepository;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ContaService contaService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(contaService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");

        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNome("Estudante Teste");
        usuarioMock.setEmail("estudante@teste.com");
        usuarioMock.setSenha("senhaAntigaHash");
    }

    @Test
    void deveGerarTokenDeRecuperacaoParaEmailValido() {
        when(usuarioRepository.findByEmail("estudante@teste.com")).thenReturn(Optional.of(usuarioMock));

        Map<String, String> resposta = contaService.solicitarRecuperacaoSenha("estudante@teste.com");

        assertNotNull(resposta);
        assertEquals("Token de recuperação gerado com sucesso.", resposta.get("message"));
        assertNotNull(resposta.get("tokenRecuperacao"));
        assertFalse(resposta.get("tokenRecuperacao").isBlank());
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoExisteNaRecuperacao() {
        when(usuarioRepository.findByEmail("inexistente@teste.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                contaService.solicitarRecuperacaoSenha("inexistente@teste.com")
        );
    }

    @Test
    void deveRedefinirSenhaComTokenValido() {
        when(usuarioRepository.findByEmail("estudante@teste.com")).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaHash");

        Map<String, String> respostaToken = contaService.solicitarRecuperacaoSenha("estudante@teste.com");

        RecuperarSenhaDTO dto = new RecuperarSenhaDTO();
        dto.setEmail("estudante@teste.com");
        dto.setTokenRecuperacao(respostaToken.get("tokenRecuperacao"));
        dto.setNovaSenha("novaSenha123");

        contaService.redefinirSenha(dto);

        assertEquals("novaSenhaHash", usuarioMock.getSenha());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }

    @Test
    void deveExcluirContaDoUsuarioAutenticado() {
        UsuarioUserDetails userDetails = new UsuarioUserDetails(usuarioMock);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        contaService.excluirConta(userDetails);

        verify(usuarioRepository, times(1)).delete(usuarioMock);
    }

    @Test
    void deveLancarExcecaoAoExcluirContaSemAutenticacao() {
        assertThrows(SecurityException.class, () -> contaService.excluirConta(null));
        verify(usuarioRepository, never()).delete(any());
    }
}
