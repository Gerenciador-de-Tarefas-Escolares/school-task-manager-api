package com.escolar.gerenciadortarefas.controller;

import com.escolar.gerenciadortarefas.dto.AvaliacaoDTO;
import com.escolar.gerenciadortarefas.model.Avaliacao;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.security.UsuarioUserDetails;
import com.escolar.gerenciadortarefas.service.AvaliacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoControllerTest {

    @Mock
    private AvaliacaoService avaliacaoService;

    @InjectMocks
    private AvaliacaoController avaliacaoController;

    private UsuarioUserDetails criarUserDetailsMock() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("aluno@teste.com");
        return new UsuarioUserDetails(usuario);
    }

    @Test
    void deveListarAvaliacoesDoUsuario() {
        UsuarioUserDetails userDetails = criarUserDetailsMock();
        Avaliacao av = Avaliacao.builder().id(1L).titulo("Prova Final").tipo("PROVA").build();
        when(avaliacaoService.listarPorUsuario(1L)).thenReturn(List.of(av));

        ResponseEntity<List<Avaliacao>> resposta = avaliacaoController.listar(userDetails);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().size());
    }

    @Test
    void deveCriarAvaliacao() {
        UsuarioUserDetails userDetails = criarUserDetailsMock();
        AvaliacaoDTO dto = new AvaliacaoDTO(
                "Seminário", null, "SEMINARIO",
                LocalDateTime.of(2026, 11, 25, 9, 0),
                null, null, null, null
        );
        Avaliacao criada = Avaliacao.builder().id(10L).titulo("Seminário").tipo("SEMINARIO").build();
        when(avaliacaoService.criar(any(AvaliacaoDTO.class), eq(1L))).thenReturn(criada);

        ResponseEntity<Avaliacao> resposta = avaliacaoController.criar(dto, userDetails);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(10L, resposta.getBody().getId());
    }

    @Test
    void deveAtualizarAvaliacao() {
        UsuarioUserDetails userDetails = criarUserDetailsMock();
        AvaliacaoDTO dto = new AvaliacaoDTO(
                "Seminário Editado", null, "SEMINARIO",
                LocalDateTime.of(2026, 11, 25, 9, 0),
                new BigDecimal("9.00"), new BigDecimal("10.00"), null, null
        );
        Avaliacao atualizada = Avaliacao.builder().id(10L).titulo("Seminário Editado").build();
        when(avaliacaoService.atualizar(eq(10L), any(AvaliacaoDTO.class), eq(1L))).thenReturn(atualizada);

        ResponseEntity<Avaliacao> resposta = avaliacaoController.atualizar(10L, dto, userDetails);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Seminário Editado", resposta.getBody().getTitulo());
    }

    @Test
    void deveExcluirAvaliacao() {
        UsuarioUserDetails userDetails = criarUserDetailsMock();

        ResponseEntity<Void> resposta = avaliacaoController.excluir(10L, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(avaliacaoService, times(1)).excluir(10L, 1L);
    }
}
