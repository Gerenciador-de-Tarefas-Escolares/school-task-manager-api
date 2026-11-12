package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.AvaliacaoDTO;
import com.escolar.gerenciadortarefas.model.Avaliacao;
import com.escolar.gerenciadortarefas.model.Materia;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.AvaliacaoRepository;
import com.escolar.gerenciadortarefas.repository.MateriaRepository;
import com.escolar.gerenciadortarefas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Test
    void deveListarAvaliacoesPorUsuario() {
        Avaliacao av = Avaliacao.builder().id(1L).titulo("Prova 1").tipo("PROVA").build();
        when(avaliacaoRepository.findByUsuarioIdOrderByDataAvaliacaoAsc(1L)).thenReturn(List.of(av));

        List<Avaliacao> resultado = avaliacaoService.listarPorUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals("Prova 1", resultado.get(0).getTitulo());
    }

    @Test
    void deveCriarAvaliacaoComNovaMateria() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Materia materiaSalva = new Materia();
        materiaSalva.setId(10L);
        materiaSalva.setNome("Cálculo I");

        AvaliacaoDTO dto = new AvaliacaoDTO(
                "Prova Bimestral",
                "Capítulos 1 a 4",
                "prova",
                LocalDateTime.of(2026, 11, 20, 10, 0),
                new BigDecimal("8.50"),
                null,
                null,
                "Cálculo I"
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.save(any(Materia.class))).thenReturn(materiaSalva);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Avaliacao salva = avaliacaoService.criar(dto, 1L);

        assertNotNull(salva);
        assertEquals("PROVA", salva.getTipo());
        assertEquals(new BigDecimal("10.00"), salva.getNotaMaxima());
        assertEquals("Cálculo I", salva.getMateria().getNome());
    }

    @Test
    void deveCriarAvaliacaoComMateriaExistente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Materia materiaExistente = new Materia();
        materiaExistente.setId(5L);
        materiaExistente.setNome("Física");

        AvaliacaoDTO dto = new AvaliacaoDTO(
                "Prova de Óptica",
                null,
                "PROVA",
                LocalDateTime.of(2026, 11, 22, 8, 0),
                null,
                new BigDecimal("10.00"),
                5L,
                null
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.findByIdAndUsuarioId(5L, 1L)).thenReturn(Optional.of(materiaExistente));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Avaliacao salva = avaliacaoService.criar(dto, 1L);

        assertNotNull(salva.getMateria());
        assertEquals(5L, salva.getMateria().getId());
        assertEquals("Física", salva.getMateria().getNome());
    }

    @Test
    void deveLancarExcecaoQuandoMateriaNaoEncontrada() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        AvaliacaoDTO dto = new AvaliacaoDTO(
                "Simulado",
                null,
                "SIMULADO",
                LocalDateTime.now(),
                null,
                null,
                99L,
                null
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> avaliacaoService.criar(dto, 1L));
        assertEquals("Matéria escolar não encontrada no banco de dados.", ex.getMessage());
    }

    @Test
    void deveAtualizarAvaliacaoComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Avaliacao existente = Avaliacao.builder()
                .id(5L)
                .titulo("Antigo")
                .tipo("PROVA")
                .usuario(usuario)
                .build();

        AvaliacaoDTO dto = new AvaliacaoDTO(
                "Título Atualizado",
                "Nova descrição",
                "trabalho",
                LocalDateTime.of(2026, 12, 1, 8, 0),
                new BigDecimal("9.50"),
                new BigDecimal("10.00"),
                null,
                null
        );

        when(avaliacaoRepository.findByIdAndUsuarioId(5L, 1L)).thenReturn(Optional.of(existente));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Avaliacao atualizada = avaliacaoService.atualizar(5L, dto, 1L);

        assertEquals("Título Atualizado", atualizada.getTitulo());
        assertEquals("TRABALHO", atualizada.getTipo());
        assertEquals(new BigDecimal("9.50"), atualizada.getNota());
    }

    @Test
    void deveBloquearAtualizacaoQuandoAvaliacaoPertenceAOutroUsuario() {
        AvaliacaoDTO dto = new AvaliacaoDTO(
                "Tentativa Invasão",
                null,
                "PROVA",
                LocalDateTime.now(),
                null,
                null,
                null,
                null
        );

        when(avaliacaoRepository.findByIdAndUsuarioId(5L, 2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> avaliacaoService.atualizar(5L, dto, 2L));
        assertEquals("Avaliação não encontrada.", ex.getMessage());
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deveExcluirAvaliacaoComSucesso() {
        Avaliacao existente = Avaliacao.builder().id(5L).build();
        when(avaliacaoRepository.findByIdAndUsuarioId(5L, 1L)).thenReturn(Optional.of(existente));

        avaliacaoService.excluir(5L, 1L);

        verify(avaliacaoRepository, times(1)).delete(existente);
    }

    @Test
    void deveBloquearExclusaoQuandoAvaliacaoPertenceAOutroUsuario() {
        when(avaliacaoRepository.findByIdAndUsuarioId(5L, 2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> avaliacaoService.excluir(5L, 2L));
        assertEquals("Avaliação não encontrada.", ex.getMessage());
        verify(avaliacaoRepository, never()).delete(any());
    }
}
