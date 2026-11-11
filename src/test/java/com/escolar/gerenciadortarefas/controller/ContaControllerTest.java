package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.RecuperarSenhaDTO;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import com.escolar.gerenciadortarefas.service.ContaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {

    @Mock
    private ContaService contaService;

    @InjectMocks
    private ContaController contaController;

    @Test
    void deveSolicitarRecuperacaoDeSenha() {
        when(contaService.solicitarRecuperacaoSenha("aluno@teste.com"))
                .thenReturn(Map.of("message", "Token gerado", "tokenRecuperacao", "abc"));

        ResponseEntity<Map<String, String>> resposta =
                contaController.esqueceuSenha(Map.of("email", "aluno@teste.com"));

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("abc", resposta.getBody().get("tokenRecuperacao"));
    }

    @Test
    void deveRedefinirSenha() {
        doNothing().when(contaService).redefinirSenha(any(RecuperarSenhaDTO.class));

        ResponseEntity<Map<String, String>> resposta =
                contaController.redefinirSenha(new RecuperarSenhaDTO());

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Senha atualizada com sucesso!", resposta.getBody().get("message"));
    }

    @Test
    void deveExcluirConta() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        UsuarioUserDetails details = new UsuarioUserDetails(usuario);

        doNothing().when(contaService).excluirConta(details);

        ResponseEntity<Void> resposta = contaController.excluirConta(details);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(contaService, times(1)).excluirConta(details);
    }
}
