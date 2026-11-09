package com.escolar.gerenciadortarefas.service;

import com.escolar.gerenciadortarefas.dto.TarefaDTO;
import com.escolar.gerenciadortarefas.model.Tarefa;
import com.escolar.gerenciadortarefas.model.Usuario;
import com.escolar.gerenciadortarefas.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaService tarefaService;

    private Tarefa tarefa;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Beatriz");
        usuario.setEmail("beatriz@email.com");

        tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTitulo("Estudar Spring Boot");
        tarefa.setStatus("PENDENTE");
        tarefa.setUsuario(usuario);
    }

    @Test
    void deveListarTarefasPorUsuarioComSucesso() {
        when(tarefaRepository.findByUsuarioId(1L)).thenReturn(List.of(tarefa));

        List<Tarefa> resultado = tarefaService.listarPorUsuario(1L);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Estudar Spring Boot", resultado.get(0).getTitulo());
        verify(tarefaRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void deveSalvarTarefaDefinindoStatusPendenteQuandoNaoConcluida() {
        when(tarefaRepository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TarefaDTO dto = new TarefaDTO();
        dto.setTitulo("Criar documentação");
        dto.setDescricao("Arquitetura");
        dto.setConcluida(false);

        Tarefa resultado = tarefaService.salvar(dto, usuario);

        assertNotNull(resultado);
        assertEquals("PENDENTE", resultado.getStatus());
        assertEquals(usuario, resultado.getUsuario());
        verify(tarefaRepository, times(1)).save(any(Tarefa.class));
    }

    @Test
    void deveLancarExecaoQuandoTarefaNaoEcontradaPorId() {
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            tarefaService.buscarPorId(99L);
        });
    }

    @Test
    void deveDeletarTarefaComSucesso() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        doNothing().when(tarefaRepository).delete(tarefa);

        assertDoesNotThrow(() -> tarefaService.deletar(1L));
        verify(tarefaRepository, times(1)).delete(tarefa);
    }
}
